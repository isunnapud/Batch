/**
 * ==========================================================================
 * Filename: SftpFileTransferUtil.java
 * =============================================================================
 * <p>
 * =============================================================================
 * <p>
 * NOTICE
 * Confidential, unpublished property of United Parcel Service.
 * Use and distribution limited solely to authorized personnel.
 * <p>
 * The use, disclosutoredre, reproduction, modification, transfer, or
 * transmittal of this work for any purpose in any form or by
 * any means without the written permission of United Parcel
 * Service is strictly prohibited.
 * <p>
 * Copyright (c) 2016-2017, United Parcel Service of America, Inc.
 * All Rights Reserved.
 * <p>
 * =============================================================================
 * Author/Architect - OMS@ups.com
 * Date Create – 11-03-2017
 * =============================================================================
 */
package com.ups.ops.oms.batch.mdc.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.ups.ops.oms.batch.mdc.constants.OmsMDCBatchConstants;

/**
 * The Class SftpFileTransferUtil.
 */
@Component
public class SftpFileTransferUtil {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(SftpFileTransferUtil.class);

	/**
	 * Transfer file method 1.
	 *
	 * @param serverAddress   the server address
	 * @param userId          the user id
	 * @param userStr         the user str
	 * @param remoteDirectory the remote directory
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 * 
	 */

	public static boolean transferFileMethod(File file, String serverAddress, String userId, String userStr,
			String remoteDirectory, String scriptCompletePath, boolean isMDCTransferEnabled, String logPath)
			throws IOException {

		String SFTPHOST = serverAddress;
		int SFTPPORT = 22;
		String SFTPUSER =userId;
		String SFTPPASS = userStr;
		String SFTPWORKINGDIR = remoteDirectory;
		String loggerPath = logPath;

		boolean execStatus = false;

		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		FileInputStream fip = null;
		ChannelExec channelExec = null;
		log.info("preparing the host information for sftp.");

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			session.setPassword(SFTPPASS);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put("trust", "true");
			session.setConfig(config);
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			session.connect();
			log.info("Host connected.");
			channel = session.openChannel("sftp");
			channel.connect();
			log.info("sftp channel opened and connected.");
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(SFTPWORKINGDIR);
			File f = new File(file.getName());
			fip = new FileInputStream(file);
			channelSftp.put(fip, f.getName());

			log.info("OMS MDC BATCH SERVICE: SFTP File upload successful");
			

			if (isMDCTransferEnabled) {

				boolean flag = mdcScriptExecution(scriptCompletePath, session, channelSftp, loggerPath);
				if (!flag) {
					log.error("Problem with Executing MDC Script");
					return flag;
				}

			}

			execStatus = true;
		} catch (Exception ex) {
			log.error("OMS MDC BATCH SERVICE: Exception during SFTP file transfer is: {}", ex);
			while (OmsMDCBatchConstants.MAX_RETRY >= OmsMDCBatchConstants.FILE_RETRY_COUNT) {
				log.info("Retrying count :{} to sftp server host: {}", OmsMDCBatchConstants.FILE_RETRY_COUNT,
						serverAddress);
				OmsMDCBatchConstants.FILE_RETRY_COUNT = OmsMDCBatchConstants.FILE_RETRY_COUNT + 1;
				closerResource(channelSftp, channelExec, channel, session, fip);
				execStatus = SftpFileTransferUtil.transferFileMethod(file, serverAddress, userId, userStr,
						remoteDirectory, scriptCompletePath, isMDCTransferEnabled, logPath);
				if (execStatus) {
					return execStatus;
				}
			}

			return execStatus = false;

		} finally {
			closerResource(channelSftp, channelExec, channel, session, fip);
		}
		return execStatus;

	}

	private static boolean mdcScriptExecution(String scriptCompletePath, Session session, ChannelSftp channelSftp,
			String loggerPath) {
		ChannelExec channelExec = null;
		boolean flag = false;
		try {
             
			channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(scriptCompletePath);
			channelExec.connect();
			int exitStatus = channelExec.getExitStatus();
			log.info("exec exitStatus {}", exitStatus);
			Thread.sleep(60000);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String date = format.format(new Date());
			String logPath = loggerPath + "_" + date + ".log";
			log.debug("mdc script log path" + logPath);
			InputStream in = channelSftp.get(logPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("Transfer completed successfully")) {
					flag = true;
					return flag;
				}
			}
			log.info("exec exitStatus {}", exitStatus);
			
			return flag;
			

		}

		catch (Exception ex) {
			log.error("Exception occurred while Excuting  MDC script file from SFTP server due to ", ex);

			while (OmsMDCBatchConstants.MAX_RETRY >= OmsMDCBatchConstants.SCRIPT_RETRY_COUNT) {
				log.info("Retrying count :{} to Execute MDC Script", OmsMDCBatchConstants.SCRIPT_RETRY_COUNT);
				OmsMDCBatchConstants.SCRIPT_RETRY_COUNT = OmsMDCBatchConstants.SCRIPT_RETRY_COUNT + 1;
				closeScriptChannel(channelExec);
				flag = mdcScriptExecution(scriptCompletePath, session, channelSftp, loggerPath);
				if (flag) {
					return flag;
				}

			}

		} finally {
			closeScriptChannel(channelExec);
		}

		return flag;

	}

	private static void closerResource(ChannelSftp channelSftp, ChannelExec channelExec, Channel channel,
			Session session, FileInputStream fip) throws IOException {
		if (channelSftp != null) {
			channelSftp.exit();

		}
		if (channelExec != null) {
			channelExec.disconnect();
		}
		if (channel != null) {
			channel.disconnect();

		}
		if (session != null) {
			session.disconnect();

		}
		if (fip != null) {
			fip.close();
		}
	}

	public static void closeScriptChannel(ChannelExec channelExec) {
		if (channelExec != null && channelExec.isConnected()) {
			channelExec.disconnect();
		}
	}

	public static boolean triggerScript(String serverAddress, String userId, String userStr, String scriptCompletePath)
			throws IOException {

		String SFTPHOST = serverAddress;
		int SFTPPORT = 22;
		String SFTPUSER = userId;
		String SFTPPASS = userStr;

		boolean execStatus = false;

		Session session = null;
		ChannelExec channelExec = null;
		log.info("preparing the host information for exec.");
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			session.setPassword(SFTPPASS);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put("trust", "true");
			session.setConfig(config);
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			session.connect();
			log.info("Host connected.");
			channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(scriptCompletePath);
			channelExec.connect();
			int exitStatus = channelExec.getExitStatus();
			log.info("exec exitStatus {}", exitStatus);
			execStatus = true;
		} catch (Exception ex) {
			log.error("OMS MDC BATCH SERVICE: Exception during cleanup trigger: {}", ex.getMessage());
			ex.printStackTrace();
			execStatus = false;
		} finally {
			if (channelExec != null) {
				channelExec.disconnect();
			}
			if (session != null) {
				session.disconnect();
				System.out.println("Host Session disconnected.");
			}
		}
		return execStatus;

	}

}

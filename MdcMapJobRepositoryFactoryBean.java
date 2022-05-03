package com.ups.ops.oms.batch.mdc.configuration;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.MapExecutionContextDao;
import org.springframework.batch.core.repository.dao.MapJobExecutionDao;
import org.springframework.batch.core.repository.dao.MapJobInstanceDao;
import org.springframework.batch.core.repository.dao.MapStepExecutionDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;

public class MdcMapJobRepositoryFactoryBean extends MapJobRepositoryFactoryBean {
	
	public MdcMapJobRepositoryFactoryBean(ResourcelessTransactionManager txManager) {
		super(txManager);
	}
	
    @Override
    public JobRepository getObject() throws Exception {
        return new MdcSimpleJobRepository(createJobInstanceDao(), createJobExecutionDao(), createStepExecutionDao(), createExecutionContextDao());
    }
 
        public class MdcSimpleJobRepository extends SimpleJobRepository {
 
        	MdcSimpleJobRepository(final JobInstanceDao jobInstanceDao,
                final JobExecutionDao jobExecutionDao,
                final StepExecutionDao stepExecutionDao,
                final ExecutionContextDao ecDao) {
            super(jobInstanceDao, jobExecutionDao, stepExecutionDao, ecDao);
        }
 
        public void clean() {
            ((MapJobInstanceDao) getJobInstanceDao()).clear();
            ((MapJobExecutionDao) getJobExecutionDao()).clear();
            ((MapStepExecutionDao) getStepExecutionDao()).clear();
            ((MapExecutionContextDao) getExecutionContextDao()).clear();
        }
 
    }
 	

}

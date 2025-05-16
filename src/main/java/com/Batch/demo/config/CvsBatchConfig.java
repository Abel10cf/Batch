package com.Batch.demo.config;

import com.Batch.demo.entity.Customer;
import com.Batch.demo.repository.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class CvsBatchConfig {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    //Create Reader
    @Bean
    public FlatFileItemReader<Customer> customerReader(){
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();

        itemReader.setResource(new FileSystemResource("C:/Users/Abel/Desktop/random_people.csv"));
        itemReader.setName("csv-reader");
        itemReader.setLinesToSkip(1); //linea o fila que se saltará
        itemReader.setLineMapper(lineMapper());

        return itemReader;
    }

    private LineMapper<Customer> lineMapper(){

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("Id","firstName", "lastName", "email");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    //Create processor
    @Bean
    public CustomerProcessor customerProcessor(){
        return new CustomerProcessor();
    }

    //Create writer
    @Bean
    public RepositoryItemWriter<Customer> customerWriter(){

        RepositoryItemWriter<Customer> repositoryWriter = new RepositoryItemWriter<>();
        repositoryWriter.setRepository(customerRepo);
        repositoryWriter.setMethodName("save");

        return repositoryWriter;
    }

    //Create step
    @Bean
    public Step step(){
        return new StepBuilder("Step-1", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(customerReader())
                //.processor(customerProcessor())
                .writer(customerWriter())
                .build();
    }

    //Create job
    @Bean
    public Job job(){
        return new JobBuilder("customers-job", jobRepository)
                .flow(step())
                .end()
                .build();
    }
}

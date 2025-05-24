package com.Batch.demo.config;

import com.Batch.demo.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

                               //cuando se implementa ItemProcessor<aquí va la salida del reader
//                                                          , aquí va lo que queremos entregar al writer>
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer item) throws Exception {
        return null;
    }
}

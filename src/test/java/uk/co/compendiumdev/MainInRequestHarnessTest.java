package uk.co.compendiumdev;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MainInRequestHarnessTest {

    /*
        A config file is a properties file where

        entity_1=value_of_entity
        entity_2=value_of_entity

        e.g.

        email_1=bob@mailinator.com
        email_2=george@mailinator.com
        twitter_1=@eviltester

     */
    @Disabled
    @Test
    public void harness(){

        String[] params = {"--config=sample.properties", "--url=https://compendiumdev.co.uk"};
        InRequestResponse.main(params);

    }
}

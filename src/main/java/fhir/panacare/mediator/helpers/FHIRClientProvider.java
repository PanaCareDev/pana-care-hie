package fhir.panacare.mediator.helpers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import fhir.panacare.mediator.config.FhirProperties;
import org.springframework.stereotype.Component;

@Component
public class FHIRClientProvider {

    private final FhirContext fhirContext;
    private final IGenericClient client;

    public FHIRClientProvider(FhirProperties fhirProperties){
        this.fhirContext = FhirContext.forR4();
        String serverUrl = fhirProperties.getServerUrl();

        if(serverUrl == null || serverUrl.isEmpty()) {
            throw new IllegalArgumentException("FHIR server URL must not be null or empty");
        }
        this.client = fhirContext.newRestfulGenericClient(serverUrl);
    }

    public FhirContext getFhirContext() {
        return fhirContext;
    }

    public IGenericClient getClient() {
        return client;
    }
}

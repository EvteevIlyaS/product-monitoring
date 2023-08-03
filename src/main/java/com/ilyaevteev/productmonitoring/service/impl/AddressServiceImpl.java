package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.exception.exceptionlist.BadRequestException;
import com.ilyaevteev.productmonitoring.model.Address;
import com.ilyaevteev.productmonitoring.repository.AddressRepository;
import com.ilyaevteev.productmonitoring.service.AddressService;
import com.kuliginstepan.dadata.client.DadataClient;
import com.kuliginstepan.dadata.client.domain.Suggestion;
import com.kuliginstepan.dadata.client.domain.address.AddressRequestBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    private final DadataClient client;

    @Override
    @Transactional
    public Address addAddress(String query) {
        Suggestion<com.kuliginstepan.dadata.client.domain.address.Address> addressSuggestion =
                client.suggestAddress(AddressRequestBuilder.create(query).build()).next().block();
        if (addressSuggestion != null) {
            com.kuliginstepan.dadata.client.domain.address.Address returnedAddress = addressSuggestion.getData();
            String city = returnedAddress.getCity();
            String street = returnedAddress.getStreet();
            String house = returnedAddress.getHouse();
            String postcode = returnedAddress.getPostalCode();

            Optional<Address> addressOptional = addressRepository.findByCityAndStreetAndHouse(city, street, house);

            if (addressOptional.isPresent()) {
                return addressOptional.get();
            } else {
                Address address = new Address();
                address.setCity(city);
                address.setStreet(street);
                address.setHouse(house);
                address.setPostcode(postcode);

                addressRepository.save(address);
                return address;
            }
        } else {
            String message = "Wrong address data";
            log.error(message);
            throw new BadRequestException(message);
        }
    }

}

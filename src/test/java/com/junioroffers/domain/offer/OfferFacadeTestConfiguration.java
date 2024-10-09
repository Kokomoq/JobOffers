package com.junioroffers.domain.offer;

import com.junioroffers.domain.offer.dto.JobOfferResponse;

import java.util.List;

public class OfferFacadeTestConfiguration {

    private final InMemoryFetcherTestImpl inMemoryFetcherTest;
    private final InMemoryOfferRepository offerRepository;

    OfferFacadeTestConfiguration() {
        this.inMemoryFetcherTest = new InMemoryFetcherTestImpl(
                List.of(
                        new JobOfferResponse("x", "x", "a", "1"),
                        new JobOfferResponse("x", "x", "a", "2"),
                        new JobOfferResponse("x", "x", "a", "3"),
                        new JobOfferResponse("x", "x", "a", "4"),
                        new JobOfferResponse("x", "x", "a", "5"),
                        new JobOfferResponse("x", "x", "a", "6")
                )
        );
        this.offerRepository = new InMemoryOfferRepository();
    }

   OfferFacadeTestConfiguration(List<JobOfferResponse> remoteClientOffers) {
        this.inMemoryFetcherTest = new InMemoryFetcherTestImpl(remoteClientOffers);
        this.offerRepository = new InMemoryOfferRepository();
    }

    OfferFacade offerFacadeForTests() {
       return new OfferFacade(offerRepository, new OfferService(inMemoryFetcherTest, offerRepository));
    }
}

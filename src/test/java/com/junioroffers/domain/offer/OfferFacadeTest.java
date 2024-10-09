package com.junioroffers.domain.offer;

import com.junioroffers.domain.offer.dto.JobOfferResponse;
import com.junioroffers.domain.offer.dto.OfferRequestDto;
import com.junioroffers.domain.offer.dto.OfferResponseDto;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class OfferFacadeTest {

    @Test
    public void shouldFetchFromJobsFromRemoteAndSaveAllOffersWhenRepositoryIsEmpty() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration().offerFacadeForTests();
        assertThat(offerFacade.findAllOffers()).isEmpty();

        // when
        List<OfferResponseDto> result = offerFacade.fetchAllOffersAndSaveAllIfNotExists();

        //then
        assertThat(result).hasSize(6);
    }

    @Test
    public void shouldSaveOnlyTwoOffersOffSixWhenRepositoryHadFourAddedOffersWithUrls() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(
                List.of(
                        new JobOfferResponse("x", "x", "a", "1"),
                        new JobOfferResponse("x", "x", "a", "2"),
                        new JobOfferResponse("x", "x", "a", "3"),
                        new JobOfferResponse("x", "x", "a", "4"),
                        new JobOfferResponse("Junior", "Sii", "10000", "https://someurl.pl/5"),
                        new JobOfferResponse("Mid", "ING", "20000", "https://someother.pl/6")
                )
        ).offerFacadeForTests();
        offerFacade.saveOffer(new OfferRequestDto("x", "x", "a", "1"));
        offerFacade.saveOffer(new OfferRequestDto("x", "x", "a", "2"));
        offerFacade.saveOffer(new OfferRequestDto("x", "x", "a", "3"));
        offerFacade.saveOffer(new OfferRequestDto("x", "x", "a", "4"));

        //when
        List<OfferResponseDto> response = offerFacade.fetchAllOffersAndSaveAllIfNotExists();

        //then
        assertThat(List.of(
                response.get(0).offerUrl(),
                response.get(1).offerUrl()
                )
        ).containsExactlyInAnyOrder("https://someurl.pl/5", "https://someother.pl/6");
    }

    @Test
    public void shouldSaveFourOffersWhenThereAreNoOffersInDatabase() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();

        //when
        offerFacade.saveOffer(new OfferRequestDto("x", "x", "a", "1"));
        offerFacade.saveOffer(new OfferRequestDto("x", "x", "a", "2"));
        offerFacade.saveOffer(new OfferRequestDto("x", "x", "a", "3"));
        offerFacade.saveOffer(new OfferRequestDto("x", "x", "a", "4"));

        //then
        assertThat(offerFacade.findAllOffers()).hasSize(4);
    }

    @Test
    public void shouldFindOfferByIdWhenOfferWasSaved() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();
        OfferResponseDto offerResponseDto = offerFacade.saveOffer(new OfferRequestDto("x", "x", "a", "1"));

        //when
        OfferResponseDto offerById = offerFacade.findOfferById(offerResponseDto.id());

        //then
        assertThat(offerById).isEqualTo(OfferResponseDto.builder()
                .id(offerResponseDto.id())
                .companyName("x")
                .position("x")
                .salary("a")
                .offerUrl("1")
                .build()
        );
    }

    @Test
    public void shouldThrowNotFoundException() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();
        assertThat(offerFacade.findAllOffers()).isEmpty();

        //when
        Throwable thrown = catchThrowable(() -> offerFacade.findOfferById("100"));

        //then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(OfferNotFoundException.class)
                .hasMessage("Offer with id 100 not found");
    }

    @Test
    public void shouldThrowDuplicateKeyExceptionWhenUrlExists() {
        //given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();
        OfferResponseDto offerResponseDto = offerFacade.saveOffer(new OfferRequestDto("x", "x", "a", "1"));
        String savedId = offerResponseDto.id();
        assertThat(offerFacade.findOfferById(savedId).id()).isEqualTo(savedId);

        //when
        Throwable thrown = catchThrowable(() -> offerFacade.saveOffer(new OfferRequestDto("x", "x", "a", "1")));

        //then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(OfferDuplicateException.class)
                .hasMessage("Offer with offerUrl [1] already exists");
    }
}
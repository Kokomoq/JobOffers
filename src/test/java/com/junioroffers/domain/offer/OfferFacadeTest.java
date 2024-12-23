package com.junioroffers.domain.offer;

import com.junioroffers.domain.offer.dto.JobOfferResponse;
import com.junioroffers.domain.offer.dto.OfferRequestDto;
import com.junioroffers.domain.offer.dto.OfferResponseDto;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;


public class OfferFacadeTest {

    @Test
    public void shouldFetchFromJobsFromRemoteAndSaveAllOffersWhenRepositoryIsEmpty() {
        // given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration().offerFacadeForTests();
        assertThat(offerFacade.findAllOffers()).isEmpty();

        // when
        List<OfferResponseDto> result = offerFacade.fetchAllOffersAndSaveAllIfNotExists();

        // then
        assertThat(result).hasSize(6);
    }

    @Test
    public void shouldSaveOnlyTwoOffersWhenRepositoryHadFourAddedWithOfferUrls() {
        // given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(
                List.of(
                        new JobOfferResponse("id", "id", "asds", "1"),
                        new JobOfferResponse("assd", "id", "asds", "2"),
                        new JobOfferResponse("asddd", "id", "asds", "3"),
                        new JobOfferResponse("asfd", "id", "asds", "4"),
                        new JobOfferResponse("Junior", "Comarch", "1000", "https://someurl.pl/5"),
                        new JobOfferResponse("Mid", "Finanteq", "2000", "https://someother.pl/6")
                )
        ).offerFacadeForTests();
        offerFacade.saveOffer(new OfferRequestDto("id", "asds", "asdasd", "1"));
        offerFacade.saveOffer(new OfferRequestDto("id", "asds", "asdasd", "2"));
        offerFacade.saveOffer(new OfferRequestDto("id", "asds", "asdasd", "3"));
        offerFacade.saveOffer(new OfferRequestDto("id", "asds", "asdasd", "4"));
        assertThat(offerFacade.findAllOffers()).hasSize(4);

        // when
        List<OfferResponseDto> response = offerFacade.fetchAllOffersAndSaveAllIfNotExists();

        // then
        assertThat(List.of(
                        response.get(0).offerUrl(),
                        response.get(1).offerUrl()
                )
        ).containsExactlyInAnyOrder("https://someurl.pl/5", "https://someother.pl/6");
    }

    @Test
    public void shouldSaveFourOffersWhenThereAreNoOffersInDatabase() {
        // given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();

        // when
        offerFacade.saveOffer(new OfferRequestDto("id", "asds", "asdasd", "1"));
        offerFacade.saveOffer(new OfferRequestDto("id", "asds", "asdasd", "2"));
        offerFacade.saveOffer(new OfferRequestDto("id", "asds", "asdasd", "3"));
        offerFacade.saveOffer(new OfferRequestDto("id", "asds", "asdasd", "4"));

        // then
        assertThat(offerFacade.findAllOffers()).hasSize(4);
    }

    @Test
    public void shouldFindOfferByIdWhenOfferWasSaved() {
        // given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();
        OfferResponseDto offerResponseDto = offerFacade.saveOffer(new OfferRequestDto("id", "asds", "asdasd", "1"));
        // when
        OfferResponseDto offerById = offerFacade.findOfferById(offerResponseDto.id());

        // then
        assertThat(offerById).isEqualTo(OfferResponseDto.builder()
                .id(offerResponseDto.id())
                .companyName("id")
                .position("asds")
                .salary("asdasd")
                .offerUrl("1")
                .build()
        );
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenOfferNotFound() {
        // given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();
        assertThat(offerFacade.findAllOffers()).isEmpty();

        // when
        Throwable thrown = catchThrowable(() -> offerFacade.findOfferById("100"));

        // then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(OfferNotFoundException.class)
                .hasMessage("Offer with id 100 not found");
    }

    @Test
    public void shouldThrowDuplicateKeyExceptionWhenWithOfferUrlExists() {
        // given
        OfferFacade offerFacade = new OfferFacadeTestConfiguration(List.of()).offerFacadeForTests();
        OfferResponseDto offerResponseDto = offerFacade.saveOffer(new OfferRequestDto("id", "asds", "asdasd", "hello.pl"));
        String savedId = offerResponseDto.id();
        assertThat(offerFacade.findOfferById(savedId).id()).isEqualTo(savedId);
        // when
        Throwable thrown = catchThrowable(() -> offerFacade.saveOffer(
                new OfferRequestDto("cx", "vc", "xcv", "hello.pl")));

        // then
        AssertionsForClassTypes.assertThat(thrown)
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("Offer with offerUrl [hello.pl] already exists");
    }
}

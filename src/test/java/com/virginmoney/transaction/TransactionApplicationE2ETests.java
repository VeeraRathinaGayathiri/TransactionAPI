package com.virginmoney.transaction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virginmoney.transaction.dto.TransactionDto;
import com.virginmoney.transaction.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionApplicationE2ETests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;


	@Test
	public void getAllTransactions_EndToEndSucccess_shouldReturnTransactionData() throws Exception {
		String response = mockMvc.perform(get("/transaction/MyMonthlyDD"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		List<TransactionDto> resultData = objectMapper.readValue(response, new TypeReference<List<TransactionDto>>() {});

		assertAll(
				() -> assertThat(resultData).isNotNull(),
				() -> assertThat(resultData.get(0))
						.extracting("id", "amount", "vendor", "category")
						.contains(1L, 40.0, "PureGym", "MyMonthlyDD")

		);
	}

	@Test
	public void getAllTransactions_CategoryNotFound_shouldReturnErrorResponse() throws Exception {
		String response = mockMvc.perform(get("/transaction/vacation"))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ErrorResponse error = objectMapper.readValue(response, new TypeReference<ErrorResponse>() {});

		assertAll(
				() -> assertThat(error.status()).isEqualTo(404),
				() -> assertThat(error.message()).isEqualTo("No transactions found for the category : vacation")
		);
	}

	@Test
	public void getTotalSpend_EndToEndSuccess_shouldReturnTotalSpend() throws Exception {

		String response = mockMvc.perform(get("/transaction/totalspend/MyMonthlyDD"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		Double resultData = objectMapper.readValue(response, Double.class);

		assertAll(
				() -> assertThat(resultData).isNotNull(),
				() -> assertThat(resultData).isEqualTo(1240.0));

	}

	@Test
	public void getTotalSpend_CategoryNotFound_shouldReturnErrorResponse() throws Exception {

		String response = mockMvc.perform(get("/transaction/totalspend/vacation"))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ErrorResponse error =  objectMapper.readValue(response, new TypeReference<ErrorResponse>() {});

		assertAll(
				() -> assertThat(error.status()).isEqualTo(404),
				() -> assertThat(error.message()).isEqualTo("No transactions found for the category : vacation")
		);

	}

	@Test
	public void getMonthlyAverage_EndToEndSuccess_shouldReturnMonthlyAverage() throws Exception {

		String response = mockMvc.perform(get("/transaction/monthlyAverage/MyMonthlyDD"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		Map<String, Double> resultdata = objectMapper.readValue(response, new TypeReference<Map<String, Double>>() {});

		assertAll(
				() -> assertThat(resultdata.size()).isEqualTo(2),
				() -> assertThat(resultdata.get("OCTOBER_2021")).isEqualTo(600.0)
		);
	}

	@Test
	public void getMonthlyAverage_CategoryNotFound_shouldReturnErrorResponse() throws Exception {

		String response = mockMvc.perform(get("/transaction/monthlyAverage/vacation"))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ErrorResponse error = objectMapper.readValue(response, new TypeReference<ErrorResponse>() {});

		assertAll(
				() -> assertThat(error.status()).isEqualTo(404),
				() -> assertThat(error.message()).isEqualTo("No transactions found for the category : vacation")
		);

	}

	@Test
	public void getHighestSpend_EndToEnd_shouldReturnHighestSpend() throws Exception {

		String response = mockMvc.perform(get("/transaction/highestSpend/MyMonthlyDD")
						.param("year", String.valueOf(2020)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		Double resultData = objectMapper.readValue(response, Double.class);

		assertAll(
				() -> assertThat(resultData).isNotNull(),
				() -> assertThat(resultData).isEqualTo(600.0));

	}

	@Test
	public void getHighestSpend_CategoryNotFound_shouldReturnErrorResponse() throws Exception {
		String response = mockMvc.perform(get("/transaction/highestSpend/vacation")
						.param("year", String.valueOf(2022)))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ErrorResponse error = objectMapper.readValue(response, new TypeReference<ErrorResponse>() {});

		assertAll(
				() -> assertThat(error.status()).isEqualTo(404),
				() -> assertThat(error.message()).isEqualTo("No transactions found for the category : vacation")
		);
	}

	@Test
	public void getHighestSpend_CategoryFound_NoTransactionForGivenYear_shouldReturnErrorResponse() throws Exception {
		String response = mockMvc.perform(get("/transaction/highestSpend/MyMonthlyDD")
						.param("year", String.valueOf(2022)))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ErrorResponse error = objectMapper.readValue(response, new TypeReference<ErrorResponse>() {});

		assertAll(
				() -> assertThat(error.status()).isEqualTo(404),
				() -> assertThat(error.message()).isEqualTo("No transaction found for this category and year combination")
		);
	}


	@Test
	public void getLowestSpend_EndToEnd_shouldReturnHighestSpend() throws Exception {

		String response = mockMvc.perform(get("/transaction/lowestSpend/MyMonthlyDD")
						.param("year", String.valueOf(2020)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		Double resultData = objectMapper.readValue(response, Double.class);

		assertAll(
				() -> assertThat(resultData).isNotNull(),
				() -> assertThat(resultData).isEqualTo(40.0));

	}

	@Test
	public void getLowestSpend_CategoryFound_NoTransactionForGivenYear_shouldReturnErrorResponse() throws Exception {
		String response = mockMvc.perform(get("/transaction/lowestSpend/MyMonthlyDD")
				.param("year", String.valueOf(2022)))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ErrorResponse error = objectMapper.readValue(response, new TypeReference<ErrorResponse>() {});

		assertAll(
				() -> assertThat(error.status()).isEqualTo(404),
				() -> assertThat(error.message()).isEqualTo("No transaction found for this category and year combination")
		);

	}

	@Test
	public void getLowestSpend_CategoryNotFound_shouldReturnErrorResponse() throws Exception {
		String response = mockMvc.perform(get("/transaction/lowestSpend/vacation")
						.param("year", String.valueOf(2022)))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResponse()
				.getContentAsString();

		ErrorResponse error = objectMapper.readValue(response, new TypeReference<ErrorResponse>() {});

		assertAll(
				() -> assertThat(error.status()).isEqualTo(404),
				() -> assertThat(error.message()).isEqualTo("No transactions found for the category : vacation")
		);

	}
}

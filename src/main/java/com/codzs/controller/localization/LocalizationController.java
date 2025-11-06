package com.codzs.controller.localization;

import com.codzs.framework.dto.localization.CodeInfoDto;
import com.codzs.framework.mapper.localization.CodeInfoMapper;
import com.codzs.framework.service.localization.LocalizationCodeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for localization code management.
 * Provides endpoints to retrieve lists of valid localization codes.
 * 
 * @author Codzs Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/localization")
@Tag(name = "Localization", description = "Localization code management APIs")
public class LocalizationController {

    private final LocalizationCodeService localizationCodeService;
    private final CodeInfoMapper codeInfoMapper;

    public LocalizationController(LocalizationCodeService localizationCodeService, CodeInfoMapper codeInfoMapper) {
        this.localizationCodeService = localizationCodeService;
        this.codeInfoMapper = codeInfoMapper;
    }

    @Operation(summary = "Get all country codes", 
               description = "Retrieves all valid ISO 3166-1 alpha-2 country codes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Country codes retrieved successfully")
    })
    @GetMapping("/countries")
    public ResponseEntity<List<CodeInfoDto>> getCountryCodes() {
        List<CodeInfoDto> countryCodes = codeInfoMapper.toDtoList(localizationCodeService.getAllCountryCodes());
        return ResponseEntity.ok(countryCodes);
    }

    @Operation(summary = "Get all currency codes", 
               description = "Retrieves all valid ISO 4217 currency codes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Currency codes retrieved successfully")
    })
    @GetMapping("/currencies")
    public ResponseEntity<List<CodeInfoDto>> getCurrencyCodes() {
        List<CodeInfoDto> currencyCodes = codeInfoMapper.toDtoList(localizationCodeService.getAllCurrencyCodes());
        return ResponseEntity.ok(currencyCodes);
    }

    @Operation(summary = "Get all timezone IDs", 
               description = "Retrieves all valid timezone identifiers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Timezone IDs retrieved successfully")
    })
    @GetMapping("/timezones")
    public ResponseEntity<List<CodeInfoDto>> getTimezones() {
        List<CodeInfoDto> timezones = codeInfoMapper.toDtoList(localizationCodeService.getAllTimezones());
        return ResponseEntity.ok(timezones);
    }

    @Operation(summary = "Get all language codes", 
               description = "Retrieves all valid ISO 639-1 language codes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Language codes retrieved successfully")
    })
    @GetMapping("/languages")
    public ResponseEntity<List<CodeInfoDto>> getLanguageCodes() {
        List<CodeInfoDto> languageCodes = codeInfoMapper.toDtoList(localizationCodeService.getAllLanguageCodes());
        return ResponseEntity.ok(languageCodes);
    }
}
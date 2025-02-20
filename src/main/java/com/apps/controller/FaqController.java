package com.apps.controller;

import com.apps.payload.request.FaqRequest;
import com.apps.payload.response.FaqResponse;
import com.apps.service.BaseService;
import com.apps.service.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FaqController extends AbstractBaseController<FaqResponse, FaqRequest, Long> {

    @Autowired
    private FaqService faqService;

    @Override
    protected BaseService<FaqResponse, FaqRequest, Long> getService() {
        return faqService;
    }

    @GetMapping("/active")
    public ResponseEntity<List<FaqResponse>> getActiveFaqs() {
        List<FaqResponse> activeFaqs = faqService.getActiveFaqs();
        return ResponseEntity.ok(activeFaqs);
    }
}

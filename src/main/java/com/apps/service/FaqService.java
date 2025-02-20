package com.apps.service;

import com.apps.model.Faq;
import com.apps.payload.request.FaqRequest;
import com.apps.payload.response.FaqResponse;
import com.apps.repository.FaqRepository;
import com.apps.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FaqService extends AbstractBaseService<Faq, FaqResponse, FaqRequest, Long> {

    @Autowired
    private FaqRepository faqRepository;

    @Override
    protected JpaRepository<Faq, Long> getRepository() {
        return faqRepository;
    }

    @Override
    protected String getEntityName() {
        return "FAQ";
    }

    @Override
    protected Faq createEntity(FaqRequest request) {
        Faq faq = new Faq();
        updateEntity(faq, request);
        return faq;
    }

    @Override
    protected void updateEntity(Faq faq, FaqRequest request) {
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        faq.setCategory(request.getCategory());
        faq.setOrderIndex(request.getOrderIndex());
        faq.setIsActive(request.getIsActive());
    }

    @Override
    protected FaqResponse convertToResponse(Faq faq) {
        return ResponseUtil.convertToResponse(faq);
    }

    @Transactional(readOnly = true)
    public List<FaqResponse> getActiveFaqs() {
        return convertList(
            faqRepository.findByIsActive(true),
            this::convertToResponse
        );
    }
}

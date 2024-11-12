package com.dev.tasks.business.service;


import com.dev.tasks.business.entity.Priority;
import com.dev.tasks.business.repository.PriorityRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PriorityService {

    private final PriorityRepository repository;

    public PriorityService(PriorityRepository repository) {
        this.repository = repository;
    }

    public List<Priority> findAll(String email) {
        return repository.findByUserEmailOrderByIdAsc(email);
    }

    public Priority add(Priority priority) {
        return repository.save(priority);
    }

    public Priority update(Priority priority) {
        return repository.save(priority);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Priority findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Priority with id " + id + " not found"));
    }

    public List<Priority> find(String title, String email) {
        return repository.find(title, email);
    }

}

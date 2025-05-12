package br.hallel.relational.api.app.ministry.service;

import br.hallel.relational.api.app.ministry.interfaces.MemberMinistryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberMinistryService {

    private final MemberMinistryRepository memberMinistryRepository;

}

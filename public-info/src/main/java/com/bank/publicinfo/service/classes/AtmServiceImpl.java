package com.bank.publicinfo.service.classes;

import com.bank.publicinfo.dto.AtmDto;
import com.bank.publicinfo.entity.AtmEntity;
import com.bank.publicinfo.exception.BadRequestException;
import com.bank.publicinfo.exception.NotFoundException;
import com.bank.publicinfo.mapper.AtmMapper;
import com.bank.publicinfo.repository.AtmRepository;
import com.bank.publicinfo.service.interfaces.AtmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AtmServiceImpl implements AtmService {
    private final AtmRepository atmRepository;
    private final AtmMapper atmMapper;

    @Override
    public AtmDto save(AtmDto atmDto) {
        if (atmDto == null) {
            throw new BadRequestException("В запросе нет данных о банкомате");
        }
        AtmEntity entity = atmRepository.save(atmMapper.toEntity(atmDto));
        try {
            log.info("Банкомат с id - \"{}\" для банка с id - \"{}\" сохранен в базе данных",
                    entity.getId(), entity.getBranch().getId());
        } catch (NullPointerException e) {
            log.info("Банкомат с id - \"{}\" без отделения банка сохранен в базе данных",
                    entity.getId());
        }
        return atmMapper.toDto(entity);
    }

    @Override
    public AtmDto findById(Long id) {
        return atmMapper.toDto(atmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Нет банкомата с таким id - " + id)));
    }

    @Override
    public void deleteByAtmIdAndBranchId(Long atmId, Long branchId) {
        try {
            atmRepository.deleteByIdAndBranch_Id(atmId, branchId);
            log.info("Банкомат с id - \"{}\" для отделения банка с id - \"{}\" удален из базы данных", atmId, branchId);
        } catch (Exception e) {
            log.error("Банкомата с id - \"{}\" для отделения банка с id - \"{}\" в базе данных не существует", atmId, branchId);
            throw new NotFoundException("Банкомата с заданными параметрами не существует");
        }
    }

    @Override
    public List<AtmDto> findAll() {
        return atmMapper.toDtoList(atmRepository.findAll());
    }

    @Override
    public List<AtmDto> findAllByBranchId(Long branchId) {
        return atmMapper.toDtoList(atmRepository.findAllByBranch_Id(branchId));
    }
}

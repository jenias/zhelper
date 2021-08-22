package ru.zhelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zhelper.models.Procurement;

@Repository
public interface ProcurementRepo extends JpaRepository<Procurement, Long> {
}

package com.example.demo.repo;

import com.example.demo.Model.MessageType;
import com.example.demo.Model.Users;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<Users, Long> {


    Users findByRollNo(Long rollNo);

    boolean existsByRollNo(Long rollNo);

    List<Users> findAllByStatus(MessageType messageType);
}

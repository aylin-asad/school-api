package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.*;
import az.iktex.organization.structure.testders.enums.Gender;
import az.iktex.organization.structure.testders.repository.*;
import az.iktex.organization.structure.testders.mapper.*;
import az.iktex.organization.structure.testders.request.*;
import az.iktex.organization.structure.testders.response.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository users;
    private final PassportRepository passports;
    private final UserMapper mapper;
    private final PassportMapper passportMapper;

    @Transactional public UserResponse create(UserRequest request) {
        if (users.existsByEmail(request.email())) throw new IllegalStateException("Email already exists");
        return mapper.toResponse(users.save(mapper.toEntity(request)));
    }
    public UserResponse get(Long id) {
        return mapper.toResponse(users.findDetailById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id)));
    }
    public UserResponse byEmail(String email) {
        return mapper.toResponse(users.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Email not found: " + email)));
    }
    public List<UserResponse> all(Gender gender) {
        return mapper.toResponseList(gender == null ? users.findAll() : users.findAllByGender(gender));
    }
    public List<UserResponse> withoutPassport() { return mapper.toResponseList(users.findAllByPassportIsNull()); }
    @Transactional public UserResponse update(Long id, UserRequest request) {
        User user = find(id);
        if (users.existsByEmailAndIdNot(request.email(), id))
            throw new IllegalStateException("Email belongs to another user");
        mapper.updateEntity(request, user); // managed -> automatic dirty checking; no save()
        return mapper.toResponse(user);
    }
    @Transactional public void delete(Long id) { users.delete(find(id)); }
    @Transactional public PassportResponse addPassport(Long id, PassportRequest request) {
        User user = find(id);
        if (user.getPassport() != null) throw new IllegalStateException("User already has a passport");
        if (passports.existsBySerialNumber(request.serialNumber()))
            throw new IllegalStateException("Passport serial number already exists");
        Passport passport = passportMapper.toEntity(request);
        user.setPassport(passport);
        passports.save(passport); // owning side gets generated ID for response
        return passportMapper.toResponse(passport);
    }
    public PassportResponse passport(Long id) {
        User user = find(id);
        if (user.getPassport() == null) throw new EntityNotFoundException("Passport not found for user " + id);
        return passportMapper.toResponse(user.getPassport());
    }
    @Transactional public void deletePassport(Long id) {
        User user = find(id);
        if (user.getPassport() == null) throw new EntityNotFoundException("Passport not found for user " + id);
        user.setPassport(null); // orphanRemoval issues DELETE; user remains.
    }
    private User find(Long id) {
        return users.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

}

package dev.mateorossello.merp.AccessModule.Mappers;

import dev.mateorossello.merp.AccessModule.DTOs.UserInput;
import dev.mateorossello.merp.AccessModule.DTOs.UserOutput;
import dev.mateorossello.merp.AccessModule.Models.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class})
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    User toEntity(UserInput user);
    
    UserOutput toOutput(User user);
    List<UserOutput> toOutputList(List<User> users);
}

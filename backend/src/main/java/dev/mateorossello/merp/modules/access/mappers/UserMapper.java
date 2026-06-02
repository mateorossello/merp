package dev.mateorossello.merp.modules.access.mappers;

import dev.mateorossello.merp.modules.access.dtos.UserInput;
import dev.mateorossello.merp.modules.access.dtos.UserOutput;
import dev.mateorossello.merp.modules.access.models.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class})
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", ignore = true)
    User toEntity(UserInput userInput);
    
    UserOutput toOutput(User user);
    List<UserOutput> toOutputList(List<User> users);
}

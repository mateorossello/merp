package dev.mateorossello.merp.modules.access.mappers;

import dev.mateorossello.merp.modules.access.dtos.ProfileInput;
import dev.mateorossello.merp.modules.access.dtos.ProfileOutput;
import dev.mateorossello.merp.modules.access.models.Profile;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TaskMapper.class})
public interface ProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "permissionsVersion", ignore = true)
    Profile toEntity(ProfileInput profileInput);
    
    ProfileOutput toOutput(Profile profile);
    List<ProfileOutput> toOutputList(List<Profile> profiles);
}

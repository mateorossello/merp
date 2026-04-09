package dev.mateorossello.merp.AccessModule.Mappers;

import dev.mateorossello.merp.AccessModule.DTOs.ProfileInput;
import dev.mateorossello.merp.AccessModule.DTOs.ProfileOutput;
import dev.mateorossello.merp.AccessModule.Models.Profile;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TaskMapper.class})
public interface ProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Profile toEntity(ProfileInput profileInput);
    
    ProfileOutput toOutput(Profile profile);
    List<ProfileOutput> toOutputList(List<Profile> profiles);
}

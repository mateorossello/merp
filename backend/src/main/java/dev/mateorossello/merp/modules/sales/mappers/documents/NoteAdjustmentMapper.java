package dev.mateorossello.merp.modules.sales.mappers.documents;

import dev.mateorossello.merp.modules.sales.dtos.documents.NoteAdjustmentInput;
import dev.mateorossello.merp.modules.sales.dtos.documents.NoteAdjustmentOutput;
import dev.mateorossello.merp.modules.sales.models.documents.NoteAdjustment;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteAdjustmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "note", ignore = true)
    NoteAdjustment toEntity(NoteAdjustmentInput noteAdjustmentInput);
    List<NoteAdjustment> toEntityList(List<NoteAdjustmentInput> noteAdjustmentInputs);

    NoteAdjustmentOutput toOutput(NoteAdjustment noteAdjustment);
    List<NoteAdjustmentOutput> toOutputList(List<NoteAdjustment> noteAdjustments);
}

package dev.mateorossello.merp.modules.sales.mappers;

import dev.mateorossello.merp.modules.sales.dtos.FiscalConfigurationInput;
import dev.mateorossello.merp.modules.sales.dtos.FiscalConfigurationOutput;
import dev.mateorossello.merp.modules.sales.models.FiscalConfiguration;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface FiscalConfigurationMapper {
    @Mapping(target = "id", ignore = true)
    FiscalConfiguration toEntity(FiscalConfigurationInput fiscalConfigurationInput);

    FiscalConfigurationOutput toOutput(FiscalConfiguration fiscalConfiguration);
    List<FiscalConfigurationOutput> toOutputList(List<FiscalConfiguration> fiscalConfigurations);
}

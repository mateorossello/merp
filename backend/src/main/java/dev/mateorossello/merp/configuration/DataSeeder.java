package dev.mateorossello.merp.configuration;

import dev.mateorossello.merp.modules.access.models.Profile;
import dev.mateorossello.merp.modules.access.models.Task;
import dev.mateorossello.merp.modules.access.models.TaskType;
import dev.mateorossello.merp.modules.access.models.User;
import dev.mateorossello.merp.modules.access.repositories.ProfileRepository;
import dev.mateorossello.merp.modules.access.repositories.TaskRepository;
import dev.mateorossello.merp.modules.access.repositories.UserRepository;
import dev.mateorossello.merp.modules.accounting.AccountingDefaults;
import dev.mateorossello.merp.modules.accounting.models.Account;
import dev.mateorossello.merp.modules.accounting.models.AccountType;
import dev.mateorossello.merp.modules.accounting.repositories.AccountRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {
    private final ProfileRepository profileRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    public DataSeeder(ProfileRepository profileRepository, TaskRepository taskRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, AccountRepository accountRepository) {
        this.profileRepository = profileRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (taskRepository.count() == 0) {
            List<Task> defaultTasks = Arrays.stream(TaskType.values()).map(type -> {
                Task task = new Task();
                task.setName(type);
                return task;
            }).toList();
            taskRepository.saveAll(defaultTasks);
        }

        if (userRepository.findByUsername("ADMINISTRATOR").isEmpty()) {
            Profile adminProfile = profileRepository.findFullByName("ADMINISTRATOR").orElseGet(() -> {
                Profile newAdminProfile = new Profile();
                newAdminProfile.setName("ADMINISTRATOR");
                return profileRepository.save(newAdminProfile);
            });

            adminProfile.getTasks().addAll(taskRepository.findAll());
            profileRepository.save(adminProfile);

            User admin = new User();
            admin.setUsername("ADMINISTRATOR");
            admin.setPassword(passwordEncoder.encode("Administrator@1"));
            admin.setProfile(adminProfile);

            userRepository.save(admin);
        }

        if (accountRepository.count() == 0) {
            // ACTIVOS
            Account activo = accountRepository.save(Account.builder().code("1").name("Activo").type(AccountType.ACTIVO).receiveBalance(false).build());
            Account activoCorriente = accountRepository.save(Account.builder().code("1.1").name("Activo Corriente").type(AccountType.ACTIVO).parentAccount(activo).receiveBalance(false).build());
            
            Account creditosVentas = accountRepository.save(Account.builder().code("1.1.2").name("Créditos por Ventas").type(AccountType.ACTIVO).parentAccount(activoCorriente).receiveBalance(false).build());
            accountRepository.save(Account.builder().code(AccountingDefaults.DEUDORES_POR_VENTAS).name("Deudores por Ventas").type(AccountType.ACTIVO).parentAccount(creditosVentas).receiveBalance(true).build());
            
            Account bienesCambio = accountRepository.save(Account.builder().code("1.1.5").name("Bienes de Cambio").type(AccountType.ACTIVO).parentAccount(activoCorriente).receiveBalance(false).build());
            accountRepository.save(Account.builder().code(AccountingDefaults.MERCADERIAS).name("Mercaderías").type(AccountType.ACTIVO).parentAccount(bienesCambio).receiveBalance(true).build());
            
            Account creditosFiscales = accountRepository.save(Account.builder().code("1.1.6").name("Créditos Fiscales").type(AccountType.ACTIVO).parentAccount(activoCorriente).receiveBalance(false).build());
            accountRepository.save(Account.builder().code(AccountingDefaults.IVA_CREDITO_FISCAL).name("IVA Crédito Fiscal").type(AccountType.ACTIVO).parentAccount(creditosFiscales).receiveBalance(true).build());

            // PASIVOS
            Account pasivo = accountRepository.save(Account.builder().code("2").name("Pasivo").type(AccountType.PASIVO).receiveBalance(false).build());
            Account pasivoCorriente = accountRepository.save(Account.builder().code("2.1").name("Pasivo Corriente").type(AccountType.PASIVO).parentAccount(pasivo).receiveBalance(false).build());
            
            Account deudasComerciales = accountRepository.save(Account.builder().code("2.1.1").name("Deudas Comerciales").type(AccountType.PASIVO).parentAccount(pasivoCorriente).receiveBalance(false).build());
            accountRepository.save(Account.builder().code(AccountingDefaults.PROVEEDORES).name("Proveedores").type(AccountType.PASIVO).parentAccount(deudasComerciales).receiveBalance(true).build());
            
            Account deudasFiscales = accountRepository.save(Account.builder().code("2.1.2").name("Deudas Fiscales").type(AccountType.PASIVO).parentAccount(pasivoCorriente).receiveBalance(false).build());
            accountRepository.save(Account.builder().code(AccountingDefaults.IVA_DEBITO_FISCAL).name("IVA Débito Fiscal").type(AccountType.PASIVO).parentAccount(deudasFiscales).receiveBalance(true).build());

            // RESULTADOS POSITIVOS
            Account resultadosPositivos = accountRepository.save(Account.builder().code("4").name("Resultados Positivos").type(AccountType.RESULTADOS_POSITIVOS).receiveBalance(false).build());
            Account resultadosPositivosVentas = accountRepository.save(Account.builder().code("4.1").name("Ingresos por Ventas").type(AccountType.RESULTADOS_POSITIVOS).parentAccount(resultadosPositivos).receiveBalance(false).build());
            
            Account ventasGrupo = accountRepository.save(Account.builder().code("4.1.1").name("Ventas").type(AccountType.RESULTADOS_POSITIVOS).parentAccount(resultadosPositivosVentas).receiveBalance(false).build());
            accountRepository.save(Account.builder().code(AccountingDefaults.VENTAS).name("Venta de Mercaderías").type(AccountType.RESULTADOS_POSITIVOS).parentAccount(ventasGrupo).receiveBalance(true).build());

            // RESULTADOS NEGATIVOS
            Account resultadosNegativos = accountRepository.save(Account.builder().code("5").name("Resultados Negativos").type(AccountType.RESULTADOS_NEGATIVOS).receiveBalance(false).build());
            Account resultadosNegativosCostosOperativos = accountRepository.save(Account.builder().code("5.1").name("Costos Operativos").type(AccountType.RESULTADOS_NEGATIVOS).parentAccount(resultadosNegativos).receiveBalance(false).build());
            
            Account cmvGrupo = accountRepository.save(Account.builder().code("5.1.1").name("CMV").type(AccountType.RESULTADOS_NEGATIVOS).parentAccount(resultadosNegativosCostosOperativos).receiveBalance(false).build());
            accountRepository.save(Account.builder().code(AccountingDefaults.CMV).name("Costo de Mercaderías Vendidas").type(AccountType.RESULTADOS_NEGATIVOS).parentAccount(cmvGrupo).receiveBalance(true).build());
            
            Account devolucionesGrupo = accountRepository.save(Account.builder().code("5.1.2").name("Devoluciones").type(AccountType.RESULTADOS_NEGATIVOS).parentAccount(resultadosNegativosCostosOperativos).receiveBalance(false).build());
            accountRepository.save(Account.builder().code(AccountingDefaults.DEVOLUCIONES_VENTAS).name("Devoluciones sobre Ventas").type(AccountType.RESULTADOS_NEGATIVOS).parentAccount(devolucionesGrupo).receiveBalance(true).build());
            
            Account ajustesGrupo = accountRepository.save(Account.builder().code("5.1.3").name("Ajustes").type(AccountType.RESULTADOS_NEGATIVOS).parentAccount(resultadosNegativosCostosOperativos).receiveBalance(false).build());
            accountRepository.save(Account.builder().code(AccountingDefaults.AJUSTES_VENTAS).name("Ajustes sobre Ventas").type(AccountType.RESULTADOS_NEGATIVOS).parentAccount(ajustesGrupo).receiveBalance(true).build());
        }
    }

    // TODO: Limpiar generación de datos automáticos para producción.
}

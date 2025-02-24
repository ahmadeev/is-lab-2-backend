package services;

import auth.Roles;
import auth.User;
import dto.utils.ImportHistoryUnitDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import objects.*;
import objects.utils.ImportHistoryUnit;
import objects.utils.ImportStatus;
import repositories.ImportHistoryRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


@Named(value = "dragonImportService")
@ApplicationScoped
public class DragonImportService {
    @Inject
    private DragonService dragonService;

    @Inject
    private ImportHistoryRepository importHistoryRepository;

    @Transactional
    public void importDragonsFromCsv(InputStream inputStream, User user) throws Exception {
        List<Dragon> dragons = new ArrayList<>();
        ImportHistoryUnit importHistoryUnit = new ImportHistoryUnit();
        importHistoryUnit.setUser(user);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Dragon dragon = parseCsvLine(line);
                // validate, exception if not valid
                dragons.add(dragon);
            }
            System.out.println("size: " + dragons.size());
            // сохраняем всех драконов в транзакции
            dragonService.createAll(dragons);
            // import history row
            importHistoryUnit.setStatus(ImportStatus.SUCCESS);
        } catch (Exception e) {
            importHistoryUnit.setStatus(ImportStatus.FAILURE);
        } finally {
            importHistoryUnit.setRowsAdded(dragons.size());
            importHistoryRepository.save(importHistoryUnit);
        }

    }

    public List<ImportHistoryUnitDTO> getImportHistory(User user, int page, int pageSize, String filterValue, String filterCol, String sortBy, String sortDir) {
        List<ImportHistoryUnitDTO> importHistory;
        if (user.getRole().equals(Roles.ADMIN)) {
            importHistory = importHistoryRepository.findAll(-1, page, pageSize, filterValue, filterCol, sortBy, sortDir);
        } else {
            importHistory = importHistoryRepository.findAll(user.getId(), page, pageSize, filterValue, filterCol, sortBy, sortDir);
        }
        return importHistory;
    }

    private Dragon parseCsvLine(String line) {
        Function<String, Boolean> isEmptyString = s -> s.isEmpty();

        String[] parts = line.split(";");

        boolean isKillerExist = false;
        for (int i = 6; i < 16; i++) {
            if (!isEmptyString.apply(parts[i])) {
                isKillerExist = true;
                break;
            }
        }

        Dragon dragon = new Dragon(
                parts[0],
                new Coordinates(
                        Long.parseLong(parts[1]),
                        Integer.parseInt(parts[2]),
                        Boolean.parseBoolean(parts[3])
                ),
                new DragonCave(
                        Float.parseFloat(parts[4]),
                        Boolean.parseBoolean(parts[5])
                ),
                isKillerExist ? new Person(
                        parts[6],
                        parts[7] == null || parts[7].isEmpty() ? null : Color.valueOf(parts[7]),
                        parts[8] == null || parts[8].isEmpty() ? null : Color.valueOf(parts[8]),
                        new Location(
                                Integer.parseInt(parts[9]),
                                Integer.parseInt(parts[10]),
                                Integer.parseInt(parts[11]),
                                Boolean.parseBoolean(parts[12])
                        ),
                        java.time.LocalDate.parse(parts[13]),
                        Integer.parseInt(parts[14]),
                        Boolean.parseBoolean(parts[15])
                ) : null,
                Long.parseLong(parts[16]),
                parts[17] == null || parts[17].isEmpty() ? null : parts[17],
                Long.parseLong(parts[18]),
                parts[19] == null || parts[19].isEmpty() ? null : DragonCharacter.valueOf(parts[19]),
                new DragonHead(
                        Float.parseFloat(parts[20]),
                        Double.parseDouble(parts[21]),
                        Boolean.parseBoolean(parts[22])
                ),
                Boolean.parseBoolean(parts[23])
        );
        System.out.println(dragon.toJson());
        return dragon;
    }


}

package ir.aut.secondhand.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ir.aut.secondhand.model.Category;
import ir.aut.secondhand.model.Location;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.repository.CategoryRepository;
import ir.aut.secondhand.repository.LocationRepository;
import ir.aut.secondhand.repository.UserRepository;

/**
 * Initializes application seed data for users, categories, and locations during startup.
 * Ensures baseline domain entities exist so that the secondhand marketplace has
 * valid administrative access, location hierarchy, and category structure before
 * the application becomes available.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            LocationRepository locationRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (locationRepository.count() == 0) {
            loadLocations();
        }

        if (categoryRepository.count() == 0) {
            loadCategories();
        }

        if (userRepository.count() == 0) {
            loadUsers();
        }
    }

    private void loadUsers() {

        User admin = new User();
        admin.setFullName("System Administrator");
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admintest123"));
        admin.setPhoneNumber("+989120000001");
        admin.setEmail("admin@test.com");
        admin.setRole(User.Role.ADMIN);
        admin.setBlocked(false);

        User user = new User();
        user.setFullName("Test Student");
        user.setUsername("user_test");
        user.setPassword(passwordEncoder.encode("usertest123"));
        user.setPhoneNumber("+989120000002");
        user.setEmail("user@test.com");
        user.setRole(User.Role.USER);
        user.setBlocked(false);

        userRepository.saveAll(List.of(admin, user));
    }

    private void loadLocations() {

        Location l1 = new Location();

        l1.setName("Tehran");
        l1.setType(Location.LocationType.STATE);
        l1.setParent(null);
        l1.setLatitude(35.6892);
        l1.setLongitude(51.3890);

        Location l2 = new Location();

        l2.setName("Isfahan");
        l2.setType(Location.LocationType.STATE);
        l2.setParent(null);
        l2.setLatitude(32.6546);
        l2.setLongitude(51.6680);

        Location l3 = new Location();

        l3.setName("Fars");
        l3.setType(Location.LocationType.STATE);
        l3.setParent(null);
        l3.setLatitude(29.5918);
        l3.setLongitude(52.5837);

        Location l4 = new Location();

        l4.setName("Khorasan Razavi");
        l4.setType(Location.LocationType.STATE);
        l4.setParent(null);
        l4.setLatitude(36.2972);
        l4.setLongitude(59.6067);

        Location l5 = new Location();

        l5.setName("East Azerbaijan");
        l5.setType(Location.LocationType.STATE);
        l5.setParent(null);
        l5.setLatitude(38.0800);
        l5.setLongitude(46.2919);

        Location l6 = new Location();

        l6.setName("Tehran");
        l6.setType(Location.LocationType.CITY);
        l6.setParent(l1);
        l6.setLatitude(35.6892);
        l6.setLongitude(51.3890);

        Location l7 = new Location();

        l7.setName("Damavand");
        l7.setType(Location.LocationType.CITY);
        l7.setParent(l1);
        l7.setLatitude(35.7214);
        l7.setLongitude(52.0700);

        Location l8 = new Location();

        l8.setName("Varamin");
        l8.setType(Location.LocationType.CITY);
        l8.setParent(l1);
        l8.setLatitude(35.3249);
        l8.setLongitude(51.6496);

        Location l9 = new Location();

        l9.setName("Isfahan");
        l9.setType(Location.LocationType.CITY);
        l9.setParent(l2);
        l9.setLatitude(32.6546);
        l9.setLongitude(51.6680);

        Location l10 = new Location();

        l10.setName("Falavarjan");
        l10.setType(Location.LocationType.CITY);
        l10.setParent(l2);
        l10.setLatitude(32.5558);
        l10.setLongitude(51.5097);

        Location l11 = new Location();

        l11.setName("Shahreza");
        l11.setType(Location.LocationType.CITY);
        l11.setParent(l2);
        l11.setLatitude(32.0089);
        l11.setLongitude(51.8700);

        Location l12 = new Location();

        l12.setName("Shiraz");
        l12.setType(Location.LocationType.CITY);
        l12.setParent(l3);
        l12.setLatitude(29.5918);
        l12.setLongitude(52.5837);

        Location l13 = new Location();

        l13.setName("Fasa");
        l13.setType(Location.LocationType.CITY);
        l13.setParent(l3);
        l13.setLatitude(28.9389);
        l13.setLongitude(53.6482);

        Location l14 = new Location();

        l14.setName("Darab");
        l14.setType(Location.LocationType.CITY);
        l14.setParent(l3);
        l14.setLatitude(28.7519);
        l14.setLongitude(54.5444);

        Location l15 = new Location();

        l15.setName("Mashhad");
        l15.setType(Location.LocationType.CITY);
        l15.setParent(l4);
        l15.setLatitude(36.2972);
        l15.setLongitude(59.6067);

        Location l16 = new Location();

        l16.setName("Sabzevar");
        l16.setType(Location.LocationType.CITY);
        l16.setParent(l4);
        l16.setLatitude(36.2166);
        l16.setLongitude(57.6754);

        Location l17 = new Location();

        l17.setName("Tabriz");
        l17.setType(Location.LocationType.CITY);
        l17.setParent(l5);
        l17.setLatitude(38.0800);
        l17.setLongitude(46.2919);

        Location l18 = new Location();

        l18.setName("Miyaneh");
        l18.setType(Location.LocationType.CITY);
        l18.setParent(l5);
        l18.setLatitude(37.4207);
        l18.setLongitude(47.7011);

        locationRepository.saveAll(List.of(
                l1, l2, l3, l4, l5,
                l6, l7, l8, l9, l10,
                l11, l12, l13, l14,
                l15, l16, l17, l18
        ));
    }

    private void loadCategories() {
        Category c1 = new Category();

        c1.setName("Digital Goods");
        c1.setParent(null);
        c1.setValidationSchema(null);
        c1.setSelectable(false);

        Category c2 = new Category();

        c2.setName("Vehicles");
        c2.setParent(null);
        c2.setValidationSchema(null);
        c2.setSelectable(false);

        Category c3 = new Category();

        c3.setName("Real Estate");
        c3.setParent(null);
        c3.setValidationSchema(null);
        c3.setSelectable(false);

        Category c4 = new Category();

        c4.setName("Home & Kitchen");
        c4.setParent(null);
        c4.setValidationSchema(null);
        c4.setSelectable(false);

        Category c5 = new Category();

        c5.setName("Laptop & Computer");
        c5.setParent(c1);
        c5.setSelectable(true);

        c5.setValidationSchema("""
                {
                  "type":"object",
                  "additionalProperties":false,
                  "properties":{
                    "brand":{
                      "type":"string",
                      "maxLength":50,
                      "label":"Brand"
                    },
                    "model":{
                      "type":"string",
                      "maxLength":50,
                      "label":"Model"
                    },
                    "color":{
                      "type":"string",
                      "maxLength":30,
                      "label":"Color"
                    },
                    "hasWarranty":{
                      "type":"boolean",
                      "label":"Has Warranty"
                    },
                    "cpuModel":{
                      "type":"string",
                      "maxLength":50,
                      "label":"CPU Model"
                    },
                    "gpuModel":{
                      "type":"string",
                      "maxLength":50,
                      "label":"GPU Model"
                    },
                    "ramGB":{
                      "type":"integer",
                      "maximum":512,
                      "label":"RAM (GB)"
                    },
                    "storageCapacity":{
                      "type":"string",
                      "maxLength":30,
                      "label":"Storage Capacity"
                    },
                    "storageType":{
                      "type":"string",
                      "enum":[
                        "HDD",
                        "SSD",
                        "OTHER"
                      ],
                      "label":"Storage Type"
                    },
                    "screenSizeInches":{
                      "type":"number",
                      "maximum":100,
                      "label":"Screen Size (Inches)"
                    }
                  }
                }
                """);
        Category c6 = new Category();

        c6.setName("Mobile & Tablet");
        c6.setParent(c1);
        c6.setSelectable(true);
        c6.setValidationSchema("""
                {
                  "type":"object",
                  "additionalProperties":false,
                  "properties":{
                    "brand":{
                      "type":"string",
                      "enum":["Apple","Samsung","Xiaomi","Huawei","Nokia","Motorola","OnePlus","Google","Sony","Honor","Other"],
                      "label":"Brand"
                    },
                    "model":{
                      "type":"string",
                      "maxLength":50,
                      "label":"Model"
                    },
                    "color":{
                      "type":"string",
                      "maxLength":30,
                      "label":"Color"
                    },
                    "hasWarranty":{
                      "type":"boolean",
                      "label":"Has Warranty"
                    },
                    "storageCapacityGB":{
                      "type":"integer",
                      "maximum":2048,
                      "label":"Storage Capacity (GB)"
                    },
                    "ramGB":{
                      "type":"integer",
                      "maximum":64,
                      "label":"RAM (GB)"
                    },
                    "batteryHealth":{
                      "type":"integer",
                      "maximum":100,
                      "label":"Battery Health (%)"
                    },
                    "simCardSlots":{
                      "type":"integer",
                      "maximum":4,
                      "label":"SIM Card Slots"
                    },
                    "operatingSystem":{
                      "type":"string",
                      "enum":["iOS","Android","Windows Phone","Symbian","HarmonyOS","Other"],
                      "label":"Operating System"
                    }
                  }
                }
                """);

        Category c7 = new Category();

        c7.setName("Game Console");
        c7.setParent(c1);
        c7.setSelectable(true);
        c7.setValidationSchema("""
                {
                  "type":"object",
                  "additionalProperties":false,
                  "properties":{
                    "brand":{
                      "type":"string",
                      "maxLength":50,
                      "label":"Brand"
                    },
                    "model":{
                      "type":"string",
                      "maxLength":50,
                      "label":"Model"
                    },
                    "color":{
                      "type":"string",
                      "maxLength":30,
                      "label":"Color"
                    },
                    "hasWarranty":{
                      "type":"boolean",
                      "label":"Has Warranty"
                    },
                    "storageCapacityGB":{
                      "type":"integer",
                      "maximum":8192,
                      "label":"Storage Capacity (GB)"
                    },
                    "includedControllers":{
                      "type":"integer",
                      "maximum":10,
                      "label":"Included Controllers"
                    }
                  }
                }
                """);

        Category c8 = new Category();

        c8.setName("Car");
        c8.setParent(c2);
        c8.setSelectable(true);
        c8.setValidationSchema("""
                {
                  "type":"object",
                  "additionalProperties":false,
                  "properties":{
                    "brand":{
                      "type":"string",
                      "maxLength":50,
                      "label":"Brand"
                    },
                    "model":{
                      "type":"string",
                      "maxLength":50,
                      "label":"Model"
                    },
                    "productionYear":{
                      "type":"integer",
                      "maximum":2100,
                      "label":"Production Year"
                    },
                    "mileageKM":{
                      "type":"integer",
                      "maximum":2000000,
                      "label":"Mileage (KM)"
                    },
                    "color":{
                      "type":"string",
                      "maxLength":30,
                      "label":"Color"
                    },
                    "gearboxType":{
                      "type":"string",
                      "enum":["Manual","Automatic"],
                      "label":"Gearbox Type"
                    },
                    "bodyStatus":{
                      "type":"string",
                      "enum":["Accident Free","Painted","Scratched","Wrecked"],
                      "label":"Body Status"
                    }
                  }
                }
                """);

        Category c9 = new Category();

        c9.setName("Motorcycle");
        c9.setParent(c2);
        c9.setSelectable(true);
        c9.setValidationSchema("""
                {
                  "type":"object",
                  "additionalProperties":false,
                  "properties":{
                    "brand":{
                      "type":"string",
                      "maxLength":50,
                      "label":"Brand"
                    },
                    "model":{
                      "type":"string",
                      "maxLength":50,
                      "label":"Model"
                    },
                    "productionYear":{
                      "type":"integer",
                      "maximum":2100,
                      "label":"Production Year"
                    },
                    "mileageKM":{
                      "type":"integer",
                      "maximum":2000000,
                      "label":"Mileage (KM)"
                    },
                    "color":{
                      "type":"string",
                      "maxLength":30,
                      "label":"Color"
                    },
                    "engineVolumeCC":{
                      "type":"integer",
                      "maximum":2500,
                      "label":"Engine Volume (CC)"
                    },
                    "motorcycleType":{
                      "type":"string",
                      "enum":["Scooter","Street","Sport","Touring","Other"],
                      "label":"Motorcycle Type"
                    }
                  }
                }
                """);
        Category c10 = new Category();

        c10.setName("Residential Sale");
        c10.setParent(c3);
        c10.setSelectable(true);
        c10.setValidationSchema("""
                {
                  "type":"object",
                  "additionalProperties":false,
                  "properties":{
                    "sizeSquareMeters":{
                      "type":"integer",
                      "maximum":100000,
                      "label":"Size (Sq Meters)"
                    },
                    "rooms":{
                      "type":"integer",
                      "maximum":100,
                      "label":"Rooms"
                    },
                    "buildYear":{
                      "type":"integer",
                      "maximum":2100,
                      "label":"Build Year"
                    },
                    "floor":{
                      "type":"integer",
                      "maximum":200,
                      "label":"Floor"
                    },
                    "hasElevator":{
                      "type":"boolean",
                      "label":"Has Elevator"
                    },
                    "hasParking":{
                      "type":"boolean",
                      "label":"Has Parking"
                    },
                    "hasWarehouse":{
                      "type":"boolean",
                      "label":"Has Warehouse"
                    },
                    "pricePerMeter":{
                      "type":"number",
                      "label":"Price Per Meter"
                    }
                  }
                }
                """);

        Category c11 = new Category();

        c11.setName("Residential Rent");
        c11.setParent(c3);
        c11.setSelectable(true);
        c11.setValidationSchema("""
                {
                  "type":"object",
                  "additionalProperties":false,
                  "properties":{
                    "sizeSquareMeters":{
                      "type":"integer",
                      "maximum":100000,
                      "label":"Size (Sq Meters)"
                    },
                    "rooms":{
                      "type":"integer",
                      "maximum":100,
                      "label":"Rooms"
                    },
                    "buildYear":{
                      "type":"integer",
                      "maximum":2100,
                      "label":"Build Year"
                    },
                    "floor":{
                      "type":"integer",
                      "maximum":200,
                      "label":"Floor"
                    },
                    "hasElevator":{
                      "type":"boolean",
                      "label":"Has Elevator"
                    },
                    "hasParking":{
                      "type":"boolean",
                      "label":"Has Parking"
                    },
                    "hasWarehouse":{
                      "type":"boolean",
                      "label":"Has Warehouse"
                    },
                    "depositAmount":{
                      "type":"number",
                      "label":"Deposit Amount"
                    },
                    "rentAmount":{
                      "type":"number",
                      "label":"Rent Amount"
                    }
                  }
                }
                """);

        Category c12 = new Category();

        c12.setName("Audio & Video");
        c12.setParent(c4);
        c12.setSelectable(true);
        c12.setValidationSchema("""
                {
                  "type":"object",
                  "additionalProperties":false,
                  "properties":{
                    "deviceType":{
                      "type":"string",
                      "enum":["TV","Radio","Monitor","Speaker","Other"],
                      "label":"Device Type"
                    },
                    "brand":{
                      "type":"string",
                      "enum":["Sony","LG","Samsung","JBL","Pioneer","XVision","DAEWOO","TSCO","Kenwood"],
                      "label":"Brand"
                    },
                    "hasRemote":{
                      "type":"boolean",
                      "label":"Has Remote"
                    }
                  }
                }
                """);

        categoryRepository.saveAll(List.of(
                c1, c2, c3, c4, c5, c6,
                c7, c8, c9, c10, c11, c12
        ));
    }
}
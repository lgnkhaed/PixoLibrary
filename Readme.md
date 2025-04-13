# our Project structure

ProjetImageManager/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── projetimagemgr/
│   │   │           ├── controller/          # Contrôleurs JavaFX (minimalistes)
│   │   │           │   ├── MainController.java
│   │   │           │   └── ImageController.java
│   │   │           ├── model/               # Classes métier
│   │   │           │   ├── ImageModel.java          # Représente une image et ses pixels
│   │   │           │   ├── Metadata.java            # Tags + historique des transformations
│   │   │           │   ├── filters/                 # Filtres et transformations
│   │   │           │   │   ├── Filter.java          # Interface générique
│   │   │           │   │   ├── GrayscaleFilter.java
│   │   │           │   │   ├── SepiaFilter.java
│   │   │           │   │   └── SobelFilter.java
│   │   │           │   └── security/                # Chiffrement/déchiffrement
│   │   │           │       └── ImageEncryptor.java
│   │   │           ├── dao/                 # Persistance (JSON/DB)
│   │   │           │   ├── MetadataDAO.java         # Gestion du fichier JSON
│   │   │           │   └── DatabaseManager.java     # Si base Derby utilisée
│   │   │           └── utils/               # Outils utilitaires
│   │   │               ├── FileUtils.java          # Gestion des ressources
│   │   │               └── HashUtils.java          # Conversion mot de passe → seed
│   │   ├── resources/
│   │   │   ├── fxml/                       # Fichiers FXML (Scene Builder)
│   │   │   │   ├── main_view.fxml
│   │   │   │   └── image_tools.fxml
│   │   │   └── images/                     # Assets (icônes, images par défaut)
│   │   │       └── placeholder.png
│   │   └── module-info.java                # Configuration des modules Java (si nécessaire)
│   └── test/                               # Tests unitaires
│       └── java/
│           └── com/
│               └── projetimagemgr/
│                   ├── model/
│                   │   └── ImageModelTest.java
│                   └── filters/
│                       └── GrayscaleFilterTest.java
├── lib/                                    # Bibliothèques externes (GSON, Derby, etc.)
├── data/                                   # Données persistantes (créé à l'exécution)
│   ├── metadata.json                      # Fichier JSON des métadonnées
│   └── derby/                             # Dossier de la base Derby (si utilisée)
├── build.gradle                           # OU pom.xml (configuration Maven)
└── README.md                              # Instructions + documentation
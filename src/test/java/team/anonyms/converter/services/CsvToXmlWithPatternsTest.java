package team.anonyms.converter.services;

/*
class ConversionCsvToXmlTest {

    private PatternService patternService;
    private ConversionFrontendService conversionFrontendService;

    private MockMultipartFile mockFile;

    private static final String inputCsv = """
        id,name,age,role,value
        1,Alice,30,admin,1
        2,Bob,25,user,0
        3,Charlie,35,moderator,1
        """;

    @BeforeEach
    void setUp() {
        patternService = Mockito.mock(PatternService.class);

        JsonMapper jsonMapper = new JsonMapper();
        XmlMapper xmlMapper = new XmlMapper();
        CsvMapper csvMapper = new CsvMapper();

        conversionFrontendService = new ConversionFrontendService(
                patternService, jsonMapper, xmlMapper, csvMapper
        );

        mockFile = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                inputCsv.getBytes()
        );
    }

    @Test
    void testConvertCsvFileToXml_IllegalPatternException() {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                null,
                null,
                null,
                null
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                List.of(modification)
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        IllegalPatternException exception = assertThrows(IllegalPatternException.class, () -> {
            conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        });
        assertEquals("Modification with null or empty oldName and newName was provided; " +
                "modification=" + modification, exception.getMessage());
    }

    @Test
    void testConvertCsvFileToXml_NewField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                null,
                "score",
                null,
                null
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<score/>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_NewField_WithValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                null,
                "score",
                null,
                "50"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<score>50</score>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_NewField_WithType_WithValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                null,
                "score",
                "Integer",
                "5267"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<score>5267</score>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_RemoveField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "age",
                null,
                null,
                null
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<age>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "age",
                null,
                null,
                "48"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<age>48</age>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_ChangeType_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "value",
                null,
                "Boolean",
                null
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<value>false</value>"));
        assertTrue(resultXml.contains("<value>true</value>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_ChangeType_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "age",
                null,
                "Boolean",
                "true"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<age>true</age>"));
        assertFalse(resultXml.contains("<age>false</age>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_RenameField_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "age",
                "years_old",
                null,
                null
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<age>"));
        assertTrue(resultXml.contains("<years_old>35</years_old>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_RenameField_ChangeType_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "value",
                "is_passed",
                "Boolean",
                null
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<value>"));
        assertTrue(resultXml.contains("<is_passed>false</is_passed>"));
        assertTrue(resultXml.contains("<is_passed>true</is_passed>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_RenameField_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "name",
                "nickname",
                null,
                "OlegMongol"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<name>Alice</name>"));
        assertFalse(resultXml.contains("<name>Bob</name>"));
        assertFalse(resultXml.contains("<name>Charlie</name>"));
        assertTrue(resultXml.contains("<nickname>OlegMongol</nickname>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_RenameField_ChangeType_ChangeValue_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification = new Modification(
                UUID.randomUUID(),
                "age",
                "BOOL",
                "Float",
                "50.5"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                List.of(modification)
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertFalse(resultXml.contains("<age>"));
        assertTrue(resultXml.contains("<BOOL>50.5</BOOL>"));

        Files.deleteIfExists(resultXmlPath);
    }

    @Test
    void testConvertCsvFileToXml_MultipleModifications_Success() throws Exception {
        UUID patternId = UUID.randomUUID();

        Modification modification_first = new Modification(
                UUID.randomUUID(),
                "age",
                null,
                "Boolean",
                "true"
        );

        Modification modification_second = new Modification(
                UUID.randomUUID(),
                null,
                "grade",
                "Integer",
                "5"
        );

        Pattern mockPattern = new Pattern(
                patternId,
                "Test Pattern",
                new ArrayList<>(List.of(modification_first, modification_second))
        );

        Mockito.when(patternService.findPatternById(patternId)).thenReturn(mockPattern);

        Path resultXmlPath = conversionFrontendService.convertCsvFileToXml(mockFile, patternId);
        String resultXml = Files.readString(resultXmlPath);

        assertTrue(resultXml.contains("<age>true</age>"));
        assertTrue(resultXml.contains("<grade>5</grade>"));
        assertFalse(resultXml.contains("<age>false</age>"));

        Files.deleteIfExists(resultXmlPath);
    }
}*/
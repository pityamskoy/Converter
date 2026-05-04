package team.anonyms.converter.services;

/*
class ConversionCsvToJsonTest {

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
    void testConvertCsvFileToJson_IllegalPatternException() {
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
            conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        });
        assertEquals("Modification with null or empty oldName and newName was provided; " +
                "modification=" + modification, exception.getMessage());
    }

    @Test
    void testConvertCsvFileToJson_NewField_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("\"score\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_NewField_WithValue_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("\"score\""));
        assertTrue(resultJson.contains("\"50\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_NewField_WithType_WithValue_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("\"score\""));
        assertTrue(resultJson.contains("5267"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_RemoveField_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"age\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_ChangeValue_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("\"48\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_ChangeType_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("false"));
        assertTrue(resultJson.contains("true"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_ChangeType_ChangeValue_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("true"));
        assertFalse(resultJson.contains("false"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_RenameField_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"age\""));
        assertTrue(resultJson.contains("\"years_old\""));
        assertTrue(resultJson.contains("35"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_RenameField_ChangeType_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"value\""));
        assertTrue(resultJson.contains("\"is_passed\""));
        assertTrue(resultJson.contains("false"));
        assertTrue(resultJson.contains("true"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_RenameField_ChangeValue_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"Alice\""));
        assertFalse(resultJson.contains("\"Bob\""));
        assertFalse(resultJson.contains("\"Charlie\""));
        assertFalse(resultJson.contains("\"name\""));

        assertTrue(resultJson.contains("\"nickname\""));
        assertTrue(resultJson.contains("\"OlegMongol\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_RenameField_ChangeType_ChangeValue_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"age\""));
        assertTrue(resultJson.contains("\"BOOL\""));
        assertTrue(resultJson.contains("50.5"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertCsvFileToJson_MultipleModifications_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertCsvFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("true"));
        assertTrue(resultJson.contains("\"grade\""));
        assertTrue(resultJson.contains("5"));
        assertFalse(resultJson.contains("false"));

        Files.deleteIfExists(resultJsonPath);
    }
}*/
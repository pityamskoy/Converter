package team.anonyms.converter.services;

/*
class ConversionJsonToCsvTest {

    private PatternService patternService;
    private ConversionFrontendService conversionFrontendService;

    private MockMultipartFile mockFile;
    private static final String inputJson = """
        [
          {
            "id": 1,
            "name": "Alice",
            "age": 30,
            "role": "admin",
            "value": 1
          },
          {
            "id": 2,
            "name": "Bob",
            "age": 25,
            "role": "user",
            "value": 0
          },
          {
            "id": 3,
            "name": "Charlie",
            "age": 35,
            "role": "moderator",
            "value": 1
          }
        ]
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
                "test.json",
                "application/json",
                inputJson.getBytes()
        );
    }

    @Test
    void testConvertJsonFileToCsv_IllegalPatternException() {
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
            conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        });
        assertEquals("Modification with null or empty oldName and newName was provided; " +
                "modification=" + modification, exception.getMessage());
    }

    @Test
    void testConvertJsonFileToCsv_NewField_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("score"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertJsonFileToCsv_NewField_WithValue_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("score"));
        assertTrue(resultCsv.contains("50"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertJsonFileToCsv_NewField_WithType_WithValue_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("score"));
        assertTrue(resultCsv.contains("5267"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertJsonFileToCsv_RemoveField_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertFalse(resultCsv.contains("age"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertJsonFileToCsv_ChangeValue_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("48"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertJsonFileToCsv_ChangeType_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("false"));
        assertTrue(resultCsv.contains("true"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertJsonFileToCsv_ChangeType_ChangeValue_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("true"));
        assertFalse(resultCsv.contains("false"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertJsonFileToCsv_RenameField_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertFalse(resultCsv.contains("age"));
        assertTrue(resultCsv.contains("years_old"));
        assertTrue(resultCsv.contains("35"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertJsonFileToCsv_RenameField_ChangeType_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertFalse(resultCsv.contains("value"));
        assertTrue(resultCsv.contains("is_passed"));
        assertTrue(resultCsv.contains("false"));
        assertTrue(resultCsv.contains("true"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertJsonFileToCsv_RenameField_ChangeValue_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);
        assertFalse(resultCsv.contains("Alice"));
        assertFalse(resultCsv.contains("Bob"));
        assertFalse(resultCsv.contains("Charlie"));
        assertTrue(resultCsv.contains("nickname"));
        assertTrue(resultCsv.contains("OlegMongol"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertJsonFileToCsv_RenameField_ChangeType_ChangeValue_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertFalse(resultCsv.contains("age"));
        assertTrue(resultCsv.contains("BOOL"));
        assertTrue(resultCsv.contains("50.5"));

        Files.deleteIfExists(resultCsvPath);
    }

    @Test
    void testConvertJsonFileToCsv_MultipleModifications_Success() throws Exception {
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

        Path resultCsvPath = conversionFrontendService.convertJsonFileToCsv(mockFile, patternId);
        String resultCsv = Files.readString(resultCsvPath);

        assertTrue(resultCsv.contains("true"));
        assertTrue(resultCsv.contains("grade"));
        assertTrue(resultCsv.contains("5"));
        assertFalse(resultCsv.contains("false"));

        Files.deleteIfExists(resultCsvPath);
    }
}*/
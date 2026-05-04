package team.anonyms.converter.services;

/*
class ConversionXmlToJsonTest {

    private PatternService patternService;
    private ConversionFrontendService conversionFrontendService;

    private MockMultipartFile mockFile;

    private static final String inputXml = """
        <ArrayList>
          <item>
            <id>1</id>
            <name>Alice</name>
            <age>30</age>
            <role>admin</role>
            <value>1</value>
          </item>
          <item>
            <id>2</id>
            <name>Bob</name>
            <age>25</age>
            <role>user</role>
            <value>0</value>
          </item>
          <item>
            <id>3</id>
            <name>Charlie</name>
            <age>35</age>
            <role>moderator</role>
            <value>1</value>
          </item>
        </ArrayList>
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
                "test.xml",
                "application/xml",
                inputXml.getBytes()
        );
    }

    @Test
    void testConvertXmlFileToJson_IllegalPatternException() {
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
            conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
        });
        assertEquals("Modification with null or empty oldName and newName was provided; " +
                "modification=" + modification, exception.getMessage());
    }

    @Test
    void testConvertXmlFileToJson_NewField_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("\"score\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertXmlFileToJson_NewField_WithValue_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("\"score\""));
        assertTrue(resultJson.contains("\"50\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertXmlFileToJson_NewField_WithType_WithValue_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("\"score\""));
        assertTrue(resultJson.contains("5267"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertXmlFileToJson_RemoveField_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"age\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertXmlFileToJson_ChangeValue_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("\"48\""));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertXmlFileToJson_ChangeType_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("false"));
        assertTrue(resultJson.contains("true"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertXmlFileToJson_ChangeType_ChangeValue_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("true"));
        assertFalse(resultJson.contains("false"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertXmlFileToJson_RenameField_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"age\""));
        assertTrue(resultJson.contains("\"years_old\""));
        assertTrue(resultJson.contains("35"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertXmlFileToJson_RenameField_ChangeType_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"value\""));
        assertTrue(resultJson.contains("\"is_passed\""));
        assertTrue(resultJson.contains("false"));
        assertTrue(resultJson.contains("true"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertXmlFileToJson_RenameField_ChangeValue_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
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
    void testConvertXmlFileToJson_RenameField_ChangeType_ChangeValue_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertFalse(resultJson.contains("\"age\""));
        assertTrue(resultJson.contains("\"BOOL\""));
        assertTrue(resultJson.contains("50.5"));

        Files.deleteIfExists(resultJsonPath);
    }

    @Test
    void testConvertXmlFileToJson_MultipleModifications_Success() throws Exception {
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

        Path resultJsonPath = conversionFrontendService.convertXmlFileToJson(mockFile, patternId);
        String resultJson = Files.readString(resultJsonPath);

        assertTrue(resultJson.contains("true"));
        assertTrue(resultJson.contains("\"grade\""));
        assertTrue(resultJson.contains("5"));
        assertFalse(resultJson.contains("false"));

        Files.deleteIfExists(resultJsonPath);
    }
}*/
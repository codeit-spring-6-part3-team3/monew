<!DOCTYPE html>
<html lang="ko" data-theme="light">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>RSS 뉴스 수집 코드 소개</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@6.4.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700;900&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Noto Sans KR', sans-serif; }
        .slide-container { 
            width: 1280px; 
            height: 720px; 
            background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
            padding: 60px;
            display: flex;
            flex-direction: column;
        }
        .info-box {
            background: white;
            border-radius: 1rem;
            padding: 20px 30px;
            width: fit-content;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
            border-left: 4px solid #3b82f6;
            transition: all 0.3s ease;
        }
        .info-box:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
        }
        .code-box {
            background: white;
            border-radius: 1rem;
            padding: 30px;
            flex: 1;
            display: flex;
            flex-direction: column;
            overflow: hidden;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
            border-left: 4px solid #10b981;
            transition: all 0.3s ease;
        }
        .code-box:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
        }
        .code-content {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            flex: 1;
            overflow: hidden;
        }
        .code-section {
            display: flex;
            flex-direction: column;
            overflow: hidden;
        }
        .section-title {
            font-size: 18px;
            font-weight: 600;
            color: #374151;
            margin-bottom: 12px;
        }
        pre {
            background: #1e293b;
            color: #e2e8f0;
            padding: 20px;
            border-radius: 12px;
            overflow: auto;
            flex: 1;
            font-family: 'Courier New', monospace;
            font-size: 13px;
            line-height: 1.6;
        }
        .explanation-box {
            background: #f8fafc;
            padding: 20px;
            border-radius: 12px;
            overflow: auto;
            flex: 1;
            border: 1px solid #e2e8f0;
        }
        .explanation-item {
            margin-bottom: 16px;
            padding-bottom: 16px;
            border-bottom: 1px solid #e2e8f0;
        }
        .explanation-item:last-child {
            border-bottom: none;
            margin-bottom: 0;
            padding-bottom: 0;
        }
        .explanation-title {
            font-size: 15px;
            font-weight: 600;
            color: #1f2937;
            margin-bottom: 6px;
        }
        .explanation-text {
            font-size: 14px;
            color: #6b7280;
            line-height: 1.5;
        }
        .info-title {
            font-size: 24px;
            font-weight: 700;
            color: #1f2937;
            margin-bottom: 5px;
        }
        .info-subtitle {
            font-size: 16px;
            font-weight: 500;
            color: #6b7280;
        }
        .code-title {
            font-size: 22px;
            font-weight: 700;
            color: #1f2937;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<div class="slide-container">
    <!-- 상단 정보 박스 -->
    <div class="info-box">
        <div class="info-title">RSS 뉴스 수집</div>
        <div class="info-subtitle">RestTemplate + StAX 스트리밍 파싱</div>
    </div>
    <!-- 소스코드 영역 -->
    <div class="code-box">
        <div class="code-title">소스코드 및 설명</div>
        <div class="code-content">
            <!-- 왼쪽: 코드 블록 -->
            <div class="code-section">
                <div class="section-title">💻 소스코드</div>
                <pre><code>
@Component
@RequiredArgsConstructor
public class HttpRssClient implements RssClient {
    private final RestTemplate restTemplate;
    private final List&lt;RssParser&gt; parsers;
    private Map&lt;ArticleSourceType, RssParser&gt; parserMap;

    @PostConstruct
    void initParserMap() {
        Map<ArticleSourceType, RssParser> map = new HashMap<>();
        for (ArticleSourceType source : ArticleSourceType.values()) {
            RssParser parser = parsers.stream()
                .filter(p -> p.supports(source))
                .findFirst()
                .orElseThrow();
            map.put(source, parser);
        }
        parserMap = Map.copyOf(map);
    }

    @Override
    public void fetchAndParse(ArticlesFeed feed, Consumer<FetchedArticles> sink) {
        String xml = restTemplate.getForObject(feed.getUrl(), String.class);
        RssParser parser = parserMap.get(feed.getSource());
        parser.parse(xml, sink); // StAX 파서로 처리, 결과는 sink로 전달
    }
}

@Slf4j
public abstract class StaxRssParser implements RssParser {
    public void parse(String xml, Consumer<FetchedArticles> sink) {
        XMLStreamReader reader = XMLInputFactory.newInstance()
            .createXMLStreamReader(new StringReader(xml));
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT
                    &amp;&amp; reader.getLocalName().equals("item")) {
                FetchedArticles article = readItem(reader);
                if (article != null) {
                    sink.accept(article);
                }
            }
        }
    }

    protected abstract FetchedArticles readItem(XMLStreamReader reader);
}
                </code></pre>
            </div>
            <!-- 오른쪽: 코드 설명 -->
            <div class="code-section">
                <div class="section-title">📝 코드 설명</div>
                <div class="explanation-box">
                    <div class="explanation-item">
                    <div class="explanation-title">1) HTTP로 RSS XML 수집</div>
                    <div class="explanation-text">RestTemplate으로 XML을 문자열로 받습니다. 필요 시 charset 보정/타임아웃/재시도 설정을 추가할 수 있습니다.</div>
                    </div>
                    <div class="explanation-item">
                    <div class="explanation-title">2) 소스별 파서 선택</div>
                    <div class="explanation-text">초기화 시 ArticleSourceType별 파서를 맵에 캐시해 빠르게 선택합니다.</div>
                    </div>
                    <div class="explanation-item">
                    <div class="explanation-title">3) StAX 스트리밍 파싱</div>
                    <div class="explanation-text">XML 전체를 메모리에 올리지 않고 item 단위로 읽어 FetchedArticles를 생성합니다.</div>
                    </div>
                    <div class="explanation-item">
                    <div class="explanation-title">4) 후속 파이프라인과 연계</div>
                    <div class="explanation-text">parse 결과는 Consumer&lt;FetchedArticles&gt;로 전달됩니다. 서비스 계층(예: ArticlesCollectService)에서 큐에 적재해 백프레셔/비동기 처리를 담당합니다.</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>

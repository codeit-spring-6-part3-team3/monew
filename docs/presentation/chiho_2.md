<!DOCTYPE html>
<html lang="ko" data-theme="light">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>개인 프로젝트 발표</title>
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
        <div class="info-title">댓글 관리</div>
        <div class="info-subtitle">댓글 CRUD + 좋아요 기능</div>
    </div>
    <!-- 소스코드 영역 -->
    <div class="code-box">
        <div class="code-title">소스코드 및 설명</div>
        <div class="code-content">
            <!-- 왼쪽: 코드 블록 -->
            <div class="code-section">
                <div class="section-title">💻 소스코드</div>
                <pre>
<span style="color: #f472b6">@RestController</span>
<span style="color: #f472b6">@RequestMapping</span>(<span style="color: #a78bfa">"/api/notifications"</span>)
<span style="color: #f472b6">@RequiredArgsConstructor</span>
<span style="color: #c084fc">public class</span> <span style="color: #fbbf24">NotificationController</span> {
    <span style="color: #c084fc">private final</span> NotificationService notificationService;
    <span style="color: #f472b6">@GetMapping</span>
    <span style="color: #c084fc">public</span> ResponseEntity&lt;Page&lt;NotificationDto&gt;&gt; <span style="color: #60a5fa">getNotifications</span>(
            <span style="color: #f472b6">@RequestParam</span>(<span style="color: #a78bfa">defaultValue = "0"</span>) <span style="color: #c084fc">int</span> page,
            <span style="color: #f472b6">@RequestParam</span>(<span style="color: #a78bfa">defaultValue = "20"</span>) <span style="color: #c084fc">int</span> size) {
        Pageable pageable = PageRequest.<span style="color: #60a5fa">of</span>(page, size);
        Page&lt;NotificationDto&gt; notifications = notificationService.<span style="color: #60a5fa">getNotifications</span>(pageable);
        <span style="color: #c084fc">return</span> ResponseEntity.<span style="color: #60a5fa">ok</span>(notifications);
    }
}</pre>
</div>
            <!-- 오른쪽: 코드 설명 -->
            <div class="code-section">
                <div class="section-title">📝 코드 설명</div>
                <div class="explanation-box">
                    <div class="explanation-item">
                        <div class="explanation-title">@RestController</div>
                        <div class="explanation-text">
                            RESTful API를 처리하는 컨트롤러임을 명시합니다. @ResponseBody가 포함되어 있어 메서드의 반환값이 자동으로 JSON으로 변환됩니다.
                        </div>
                    </div>
                    <div class="explanation-item">
                        <div class="explanation-title">@RequestMapping("/api/notifications")</div>
                        <div class="explanation-text">
                            이 컨트롤러의 모든 엔드포인트가 /api/notifications 경로 아래에 매핑됩니다.
                        </div>
                    </div>
                    <div class="explanation-item">
                        <div class="explanation-title">커서 페이지네이션 처리</div>
                        <div class="explanation-text">
                            page와 size 파라미터를 받아 Pageable 객체를 생성하고, 서비스 레이어에서 페이징 처리된 알림 목록을 조회합니다. 기본값은 0페이지, 20개씩 조회입니다.
                        </div>
                    </div>
                    <div class="explanation-item">
                        <div class="explanation-title">ResponseEntity 반환</div>
                        <div class="explanation-text">
                            HTTP 상태 코드와 함께 응답을 반환합니다. 200 OK 상태와 함께 알림 목록을 JSON 형태로 클라이언트에 전달합니다.
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div></body>


<!--==========================================
수정 가이드
==========================================

1. 정보 박스 수정:
    - "어디 파트인지" → 프로젝트 파트명으로 변경 (예: "백엔드 API", "알림 시스템")
    - "위하는 파트인지" → 구체적인 기능 설명으로 변경 (예: "실시간 알림 처리", "커서 기반 페이지네이션")

2. 소스코드 제목 변경:
    - "소스코드 및 설명" → 원하는 제목으로 변경

3. 왼쪽 - 소스코드 변경:
    - <pre> 태그 안의 코드를 본인의 코드로 교체
    - 코드 색상 유지하려면 기존 <span style="color: ..."> 태그 사용

4. 오른쪽 - 코드 설명 추가/수정:
   설명 항목 추가는 아래 템플릿 복사:

   <div class="explanation-item">
       <div class="explanation-title">설명 제목</div>
       <div class="explanation-text">
           설명 내용을 여기에 작성하세요.
       </div>
   </div>

5. 보더 색상 변경 (Q&A 페이지와 통일된 스타일):
   정보 박스:
    - border-left: 4px solid #3b82f6 (파란색, 현재)
    - 다른 색상: #8b5cf6 (보라), #f59e0b (주황), #ef4444 (빨강)

   코드 박스:
    - border-left: 4px solid #10b981 (초록색, 현재)
    - 다른 색상: #3b82f6 (파랑), #8b5cf6 (보라), #06b6d4 (청록)

==========================================
레이아웃 특징
==========================================
✓ 2열 구조: 왼쪽 코드 + 오른쪽 설명
✓ 양쪽 모두 스크롤 가능
✓ 코드는 다크 테마, 설명은 밝은 배경
✓ 설명 항목은 구분선으로 구분

==========================================
코드 하이라이트 색상 참고
==========================================
어노테이션: #f472b6 (분홍)
키워드: #c084fc (보라)
문자열: #a78bfa (연보라)
함수/메서드: #60a5fa (파랑)
클래스명: #fbbf24 (노랑)
주석: #6b7280 (회색)-->
</html>
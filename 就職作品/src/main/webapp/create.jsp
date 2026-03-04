<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%
    // Servletから渡されたデータを取得
    String strStart = (String)request.getAttribute("START");
    String strPlace = (String)request.getAttribute("PLACE");
    String strLast = (String)request.getAttribute("LAST");
    String strMembers = (String)request.getAttribute("MEMBERS");
    Integer travelId = (Integer)request.getAttribute("TRAVEL_ID");
    Integer nights = (Integer)request.getAttribute("NIGHTS");
    Integer days = (Integer)request.getAttribute("DAYS");
    
    @SuppressWarnings("unchecked")
    List<Map<String, String>> dateList = (List<Map<String, String>>)request.getAttribute("DATE_LIST");
    
    if (dateList == null) {
        dateList = new ArrayList<>();
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>旅行スケジュール | TravelExplorer</title>
    <link rel="stylesheet" href="css/create.css">
    <link rel="stylesheet" href="css/createtab.css">
</head>
<body>
    <header class="navbar">
        <div class="logo">
            <span class="logo-icon">✈️</span>
            <span class="logo-text">Tripスケジュール</span>
        </div>
        <nav>
            <a href="triplist.jsp">ホーム</a>
            <a href="TripListServlet" class="active">旅行計画</a>
<!--            <button class="signup-btn">新規登録</button>
        </nav>-->
        
        </nav>
        </header>
        

    <main class="container">
        <!-- 旅行情報カード -->
        <div class="trip-info-card">
            <div class="trip-info-item">
                <span class="icon blue">📍</span>
                <div class="trip-info-content">
                    <div class="trip-info-label">旅行先</div>
                    <div class="trip-info-value"><%=strPlace %></div>
                </div>
            </div>

            <div class="trip-info-item">
                <span class="icon blue">📅</span>
                <div class="trip-info-content">
                    <div class="trip-info-label">滞在期間</div>
                    <div class="trip-info-value"><%=strStart %> 〜 <%=strLast %> <span class="badge"><%=nights %>泊<%=days %>日</span></div>
                </div>
            </div>

            <div class="trip-info-item">
                <span class="icon blue">👤</span>
                <div class="trip-info-content">
                    <div class="trip-info-label">人数</div>
                    <div class="trip-info-value"><%=strMembers %>人</div>
                </div>
            </div>
        </div>

        <!-- スケジュールセクション -->
        <div class="schedule-section">
            <div class="schedule-header">
                <div class="schedule-title">
                    <span class="icon">📅</span>
                    <h2>旅行スケジュール</h2>
                </div>
                <div>
                    <button type="button" class="save-schedule-btn" id="saveScheduleBtn">
                    💾 スケジュールを保存
                    </button>
                    
<!--                    <button type="button" class="share-schedule-btn"-->
<!--        onclick="location.href='TripListServlet'">-->
<!--  ← 旅行スケジュール一覧-->
<!--</button>-->

<!--                    <button type="button" class="share-schedule-btn" id="shareScheduleBtn">-->
<!--                        🔗 共有-->
<!--                    </button>-->
<!--                    <button type="button" class="share-schedule-btn" id="saveBtnTemp">-->
<!--                        📝 保存-->
<!--                    </button>-->
                </div>
            </div>

            <p class="schedule-description">時間ごとの予定を追加して旅行計画を立てましょう</p>

            <!-- 日付タブ -->
            <div class="date-tabs">
                <% for (int i = 0; i < dateList.size(); i++) {
                    Map<String, String> dateInfo = dateList.get(i);
                    String activeClass = (i == 0) ? "active" : "";
                %>
                    <div class="date-tab <%= activeClass %>" onclick="switchDay(<%= i %>)" data-day="<%= i %>">
                        <div class="date-tab-label"><%= dateInfo.get("formatted") %></div>
                        <div class="date-tab-day"><%= (i + 1) %>日目</div>
                    </div>
                <% } %>
            </div>

            <!-- 各日のスケジュール -->
            <% for (int i = 0; i < dateList.size(); i++) {
                Map<String, String> dateInfo = dateList.get(i);
                String activeClass = (i == 0) ? "active" : "";
            %>
                <div class="day-content <%= activeClass %>" id="day-<%= i %>">
                    <div class="day-header">
                        <div class="day-title">
                            <span class="day-number"><%= (i + 1) %>日目</span>
                            <span class="day-date-info"><%= dateInfo.get("year") %>年<%= dateInfo.get("formatted") %></span>
                        </div>
                        <button type="button" class="add-schedule-btn" onclick="openModalForDay(<%= i %>, '<%= dateInfo.get("date") %>')">
                            <span class="plus-icon">+</span>
                            予定を作成
                        </button>
                    </div>

                    <!-- スケジュール一覧 -->
                    <div class="schedule-list" id="scheduleList-<%= i %>">
                        <!-- 空の状態 -->
                        <div class="empty-state" id="emptyState-<%= i %>">
                            <div class="empty-icon">📅</div>
                            <p class="empty-text">この日の予定はまだありません</p>
                            <p class="empty-subtext">「予定を作成」から予定を追加しましょう</p>
                        </div>
                    </div>
                </div>
            <% } %>
        </div>
    </main>

    <!-- ブロック追加モーダル -->
    <div class="modal" id="addBlockModal">
        <div class="modal-overlay" id="modalOverlay"></div>
        <div class="modal-content">
            <div class="modal-header">
                <h3>新しい予定を追加</h3>
                <button class="modal-close" id="modalClose">×</button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="scheduleDate" value="">
                
                <div class="form-group">
                    <label>時間</label>
                    <div class="time-input-group">
                        <input type="time" id="startTime" value="09:00">
                        <span class="separator">〜</span>
                        <input type="time" id="endTime" value="10:00">
                    </div>
                </div>

                <div class="form-group">
                    <label>タイトル</label>
                    <input type="text" id="scheduleTitle" placeholder="予定のタイトルを入力" class="form-input">
                </div>

                <div class="form-group">
                    <label>詳細</label>
                    <textarea id="scheduleDescription" placeholder="詳細な説明（任意）" class="form-textarea" rows="3"></textarea>
                </div>

                <button type="button" class="submit-btn" id="submitSchedule">
                    <span class="plus-icon">+</span>
                    予定を追加
                </button>
            </div>
        </div>
    </div>

    <!-- 共有モーダル -->
    <div class="modal" id="shareModal">
        <div class="modal-overlay" id="shareModalOverlay"></div>
        <div class="modal-content share-modal">
            <div class="modal-header">
                <div class="modal-title-with-icon">
                    <span class="share-icon">🔗</span>
                    <h3>スケジュールを共有</h3>
                </div>
                <button class="modal-close" id="shareModalClose">×</button>
            </div>
            <div class="modal-body">
                <p class="share-description">グループメンバーとスケジュールを共有して、一緒に旅行計画を立てましょう</p>
                
                <div class="tabs">
                    <button class="tab-btn active" data-tab="link">
                        <span class="tab-icon">🔗</span>
                        リンク
                    </button>
                    <button class="tab-btn" data-tab="email">
                        <span class="tab-icon">✉️</span>
                        招待
                    </button>
                    <button class="tab-btn" data-tab="qr">
                        <span class="tab-icon">📱</span>
                        QRコード
                    </button>
                </div>

                <div class="tab-content active" id="linkTab">
                    <div class="form-group">
                        <label>共有リンク</label>
                        <div class="share-link-box">
                            <input type="text" id="shareLink" 
                                   value="https://travelexplorer.com/schedule/schedule-1764057856451" 
                                   readonly class="share-link-input">
                            <button class="copy-btn" id="copyLinkBtn">
                                <span id="copyIcon">📋</span>
                            </button>
                        </div>
                        <p class="help-text">このリンクを共有することで、誰でもスケジュールを閲覧できます</p>
                    </div>

                    <div class="info-box">
                        <div class="info-icon">🔗</div>
                        <div>
                            <p class="info-title">リンクの権限設定</p>
                            <p class="info-description">このリンクを持っている人は、スケジュールの閲覧と編集ができます</p>
                        </div>
                    </div>
                </div>

                <div class="tab-content" id="emailTab">
                    <div class="form-group">
                        <label>メールアドレス</label>
                        <div class="email-input-group">
                            <input type="email" id="inviteEmail" 
                                   placeholder="example@email.com" 
                                   class="form-input">
                            <button class="invite-btn" id="sendInviteBtn">
                                <span class="plus-icon">+</span>
                                招待
                            </button>
                        </div>
                        <p class="help-text">招待メールを送信してグループに追加します</p>
                    </div>

                    <div class="invited-members" id="invitedMembers">
                        <p class="section-label">グループメンバー (0)</p>
                        <div class="empty-members">
                            <p>まだメンバーがいません</p>
                        </div>
                    </div>
                </div>

                <div class="tab-content" id="qrTab">
                    <div class="qr-container">
                        <div class="qr-code-box">
                            <div class="qr-code-placeholder">
                                <span class="qr-icon">📱</span>
                            </div>
                        </div>
                        <p class="qr-description">
                            スマートフォンでQRコードをスキャンして<br>
                            スケジュールにアクセス
                        </p>
                        <button class="copy-link-btn" id="copyLinkFromQR">
                            <span>📋</span>
                            リンクをコピー
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    
   <script>
  // ★ window に入れる（Create.js が参照する）
  window.dateList = [
    <% for (int i = 0; i < dateList.size(); i++) { %>
      "<%= dateList.get(i).get("date") %>"<%= (i < dateList.size() - 1) ? "," : "" %>
    <% } %>
  ];

  window.travelId = <%= travelId != null ? travelId : 0 %>;

  // ★ window に入れる（Create.js が参照する）
  window.initialSchedules = [
    <%
      List<Map<String,String>> scheduleList =
        (List<Map<String,String>>)request.getAttribute("SCHEDULE_LIST");
      if (scheduleList != null) {
        for (int i=0; i<scheduleList.size(); i++) {
          Map<String,String> s = scheduleList.get(i);
    %>
    {
    	id: <%= i+1 %>,
      date: "<%= s.get("date") %>",
      startTime: "<%= s.get("startTime") %>",
      endTime: "<%= s.get("endTime") %>",
      title: "<%= s.get("title").replace("\"","\\\"") %>",
      description: "<%= (s.get("description")==null?"":s.get("description")).replace("\"","\\\"") %>"
    }<%= (i < scheduleList.size()-1) ? "," : "" %>
    <%
        }
      }
    %>
  ];
</script>
   

<script src="js/create.js"></script>
    </body>
</html>
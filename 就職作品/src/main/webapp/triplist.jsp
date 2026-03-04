<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.time.*" %>
<%@ page import="java.time.format.*" %>
<%@ page import="java.time.temporal.ChronoUnit" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>保存した旅行スケジュール | TravelExplorer</title>
  <link rel="stylesheet" href="css/triplist.css">
</head>
<body>
<header class="navbar">
  <div class="logo">
    <span class="logo-icon">✈️</span>
    <span class="logo-text">Tripスケジュール</span>
  </div>
  <nav class="nav-links">
    <a href="home.jsp">ホーム</a>
    <a href="TripListServlet" class="active">旅行計画</a>
<!--    <button class="signup-btn" type="button">新規登録</button>-->
  </nav>
</header>

<div class="back-link-area">
<!--    <a href="<%= request.getContextPath() %>/TripListServlet" class="back-link">-->
<!--        ← 旅行スケジュール一覧-->
<!--    </a>-->
</div>

<main class="container">
  <div class="header-section">
    <div class="page-title">
      <span class="folder-icon">📁</span>
      <div>
        <h1>保存した旅行スケジュール</h1>
        <p class="page-description">作成した旅行スケジュールを確認・編集できます</p>
      </div>
    </div>

    <!-- 画像の右上の「＋ 新しい旅行を作成」 -->
    <a href="home.jsp" class="create-new-btn">
      <span class="plus-icon">+</span>
      新しい旅行を作成
    </a>
  </div>

  <section class="trip-list">
    <%
      Boolean isEmptyObj = (Boolean) request.getAttribute("isEmpty");
      boolean isEmpty = (isEmptyObj != null) ? isEmptyObj.booleanValue() : true;

      List<Map<String, Object>> tripList =
          (List<Map<String, Object>>) request.getAttribute("tripList");
    %>

    <% if (isEmpty || tripList == null || tripList.isEmpty()) { %>
      <!-- 空状態（画像1枚目） -->
      <div class="empty-state">
        <div class="empty-icon">📁</div>
        <p class="empty-text">まだ旅行スケジュールがありません</p>
        <p class="empty-subtext">「最初の旅行を作成」から始めましょう</p>
<!--        <a class="primary-btn" href="Home.jsp">-->
<!--          <span class="plus-icon">+</span>-->
<!--          最初の旅行を作成-->
<!--        </a>-->
      </div>
    <% } else { %>

      <div class="grid">
        <%
          for (Map<String, Object> trip : tripList) {

            int travelId = (Integer) trip.get("id");
            String destination = String.valueOf(trip.get("destination"));
            String startDateStr = String.valueOf(trip.get("startDate")); // 例: 2026-01-23
            String endDateStr = String.valueOf(trip.get("endDate"));     // 例: 2026-01-26
            String members = String.valueOf(trip.get("members"));
            String createdAtFormatted = String.valueOf(trip.get("createdAtFormatted"));

            // 泊数計算（end - start）
            int nights = 0;
            try {
              LocalDate s = LocalDate.parse(startDateStr);
              LocalDate e = LocalDate.parse(endDateStr);
              nights = (int) ChronoUnit.DAYS.between(s, e);
              if (nights < 0) nights = 0;
            } catch (Exception ignore) {}

            // 予定数など（Servletで入れているなら表示。なければ0）
            int scheduleCount = 0;
            Object sc = trip.get("scheduleCount");
            if (sc instanceof Integer) scheduleCount = (Integer) sc;

            int photoCount = 0;
            Object pc = trip.get("photoCount");
            if (pc instanceof Integer) photoCount = (Integer) pc;
        %>

        <article class="trip-card" data-travel-id="<%= travelId %>">
          <div class="trip-info">
            <div class="trip-header">
              <span class="location-icon">📍</span>
              <h2 class="trip-title"><%= destination %></h2>
            </div>

            <p class="created-date"><%= createdAtFormatted %> に作成</p>

            <div class="trip-details">
              <div class="detail-item">
                <span class="detail-icon">📅</span>
                <span class="detail-text"><%= startDateStr %> 〜 <%= endDateStr %></span>
                <span class="badge"><%= nights %>泊</span>
              </div>

              <div class="detail-item">
                <span class="detail-icon">👤</span>
                <span class="detail-text"><%= members %>人</span>
              </div>
            </div>

<!--            <div class="trip-stats">-->
<!--              <span><%= scheduleCount %>件の予定</span>-->
<!--              <span class="dot">•</span>-->
<!--              <span><%= photoCount %>枚の写真</span>-->
<!--            </div>-->
<!--          </div>-->

          <button class="delete-btn" type="button" data-delete-id="<%= travelId %>" aria-label="削除">
            🗑️
          </button>
        </article>

        <%
          } 
        %>
      </div>
    <% } %>
  </section>
</main>

<!--<div class="help-button">-->
<!--  <button class="help-btn" type="button">?</button>-->
<!--</div>-->

<script src="js/triplist.js"></script>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>TripSchedule</title>
<link rel="stylesheet" href="css/home.css">
</head>
<body>
<form action="./HomeServlet" method="get">

    <header class="navbar">
        <div class="logo">✈️ Tripスケジュール</div>

<!--        <nav>-->
<!--            <a href="#">ホーム</a>-->
<!--            <a href="#">旅行計画</a>-->
<!--            <a href="#">ログイン</a>-->
<!--            <a href="#">新規登録</a>-->
<!--         </nav>-->
    </header>

    
    <section class="hero">
        <h2>次の旅行先を見つけよう</h2>
        <p>世界中の素晴らしい旅行先があなたを待っています</p>

        
        <div class="search-card">

            
            <div class="input-row">
                <div class="input-box">
                    <span class="icon">📍</span>
                    <input id="place" type="text" name="place" placeholder="旅行先">
                </div>
            </div>

            
            <div class="input-row">
                <div class="input-box">
                    <span class="icon">📅</span>
                    <input id="startDate" type="date" name="start">
                    
                </div>

                <div class="input-box">
                    <span class="icon">📅</span>
                    <input id="endDate" type="date" name="last">
                </div>

                <div class="input-box">
                    <span class="icon">👤</span>
                    <select id="members" name="members">
                    <option value="">-------</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5以上</option>
                    </select>
                </div>
            </div>

            
<!--            <form action="CreateServlet" method="get">-->
<!--    <input type="hidden" name="planId" value="${plan.id}">-->
<!--    <button type="submit">タイムスケジュールを作成</button>-->

<form action="HomeServlet" method="post">
  <!-- 旅行先、start、last、members の input/select -->
  <button id="createBtn" type="submit" class="main-btn">
    タイムスケジュール作成
  </button>
</form>


            

 <script src="js/home.js">

   </script>    </section>
   </form>
</body>
</html>
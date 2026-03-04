/**
 * 
 */
// DOM 読み込み後に実行
document.addEventListener("DOMContentLoaded", () => {

    const submitBtn = document.getElementById("createBtn");

    submitBtn.addEventListener("click", () => {

        // 各入力値の取得
        const place = document.getElementById("place").value;
        const start = document.getElementById("startDate").value;
        const end = document.getElementById("endDate").value;
        const members = document.getElementById("members").value;

        // 入力チェック
        if (!place || !start || !end) {
            alert("旅行先・日付をすべて入力してください");
            return;
        }

        // データまとめ
        const tripData = {
            destination: place,
            start_date: start,
            end_date: end,
            members: members
        };

        // ▼ 確認用（本番ではAPI送信などに変更）
        console.log("▼ 入力された旅行計画データ");
        console.table(tripData);

        alert(
            `旅行計画を作成します！\n\n` +
            `旅行先：${place}\n` +
            `出発日：${start}\n` +
            `終了日：${end}\n` +
            `人数　：${members}人`
        );

        // ▼ 今後追加予定：バックエンドへ送信する場合
        // fetch("/api/trip", {
        //     method: "POST",
        //     headers: { "Content-Type": "application/json" },
        //     body: JSON.stringify(tripData)
        // });
    });
	// DOM 読み込み後に実行
	document.addEventListener("DOMContentLoaded", () => {
	    console.log("home.js 読み込み完了");
	    
	    // フォーム送信前のバリデーション（オプション）
	    const form = document.querySelector("form");
	    
	    if (form) {
	        form.addEventListener("submit", (e) => {
	            const place = document.getElementById("place").value.trim();
	            const start = document.getElementById("startDate").value;
	            const end = document.getElementById("endDate").value;
	            const members = document.getElementById("members").value;
	            
	            // 入力チェック
	            if (!place || !start || !end || !members) {
	                alert("すべての項目を入力してください");
	                e.preventDefault(); // フォーム送信を中止
	                return false;
	            }
	            
	            // 日付の整合性チェック
	            if (new Date(start) > new Date(end)) {
	                alert("終了日は出発日より後の日付を選択してください");
	                e.preventDefault();
	                return false;
	            }
	            
	            console.log("フォーム送信:", { place, start, end, members });
	            // フォームは通常通り送信される
	        });
	    }
	});

});
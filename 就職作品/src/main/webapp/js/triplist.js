/**
 * 
 */
document.addEventListener("DOMContentLoaded", () => {
  // カードクリック → ViewTripServletへ
  document.querySelectorAll(".trip-card").forEach(card => {
    card.addEventListener("click", (e) => {
      // 削除ボタンを押した時はカード遷移しない
      if (e.target.closest(".delete-btn")) return;

      const travelId = card.dataset.travelId;
      if (travelId) {
        location.href = "ViewTripServlet?travelId=" + encodeURIComponent(travelId);
      }
    });
  });

  // 削除ボタン
  document.querySelectorAll(".delete-btn").forEach(btn => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      e.stopPropagation();

      const travelId = btn.dataset.deleteId;
      if (!travelId) return;

      const ok = confirm("この旅行スケジュールを削除しますか？");
      if (ok) {
        location.href = "DeleteTripServlet?travelId=" + encodeURIComponent(travelId);
      }
    });
  });
});

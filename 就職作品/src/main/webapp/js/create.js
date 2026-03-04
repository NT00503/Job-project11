// =========================
// Create.js（完全版 / 置換用）
// =========================

// =========================
// TravelExplorer Create.js（統一・安定版）
// =========================

let schedules = [];
let editingId = null;
let currentDayIndex = 0;

document.addEventListener("DOMContentLoaded", () => {
  // 初期データ（DBから）
  if (Array.isArray(window.initialSchedules)) {
    schedules = window.initialSchedules.map((s, idx) => ({
      id: Date.now() + idx,
      date: s.date,
      startTime: s.startTime,
      endTime: s.endTime,
      title: s.title,
      description: s.description || ""
    }));
  }

  initEventListeners();

  // 初期表示
  if (Array.isArray(window.dateList) && window.dateList.length > 0) {
    switchDay(0);
  }
  renderSchedules();
});

function initEventListeners() {
  // モーダル閉じる
  const modalOverlay = document.getElementById("modalOverlay");
  const modalClose = document.getElementById("modalClose");
  if (modalOverlay) modalOverlay.addEventListener("click", closeModal);
  if (modalClose) modalClose.addEventListener("click", closeModal);

  // 追加確定
  const submit = document.getElementById("submitSchedule");
  if (submit) submit.addEventListener("click", handleSubmit);

  // 保存（DB）
  const saveBtn = document.getElementById("saveScheduleBtn");
  if (saveBtn) saveBtn.addEventListener("click", submitToSaveServlet);

  // Escで閉じる
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") closeModal();
  });
}

// -------------------------
// タブ切替
// -------------------------
function switchDay(dayIndex) {
  currentDayIndex = dayIndex;

  document.querySelectorAll(".date-tab").forEach(t => t.classList.remove("active"));
  document.querySelectorAll(".day-content").forEach(c => c.classList.remove("active"));

  const tab = document.querySelector(`.date-tab[data-day="${dayIndex}"]`);
  const content = document.getElementById(`day-${dayIndex}`);

  if (tab) tab.classList.add("active");
  if (content) content.classList.add("active");
}

function openModalForDay(dayIndex, date) {
  currentDayIndex = dayIndex;
  const hiddenDate = document.getElementById("scheduleDate");
  if (hiddenDate) hiddenDate.value = date;
  openModalForAdd();
}

// -------------------------
// モーダル
// -------------------------
function openModalForAdd() {
  editingId = null;

  const modal = document.getElementById("addBlockModal");
  const scheduleDate = document.getElementById("scheduleDate");

  // hidden date が空なら現在タブの日付
  if (scheduleDate && !scheduleDate.value) {
    if (Array.isArray(window.dateList) && window.dateList.length > 0) {
      scheduleDate.value = window.dateList[currentDayIndex] || window.dateList[0];
    }
  }

  document.getElementById("startTime").value = "09:00";
  document.getElementById("endTime").value = "10:00";
  document.getElementById("scheduleTitle").value = "";
  document.getElementById("scheduleDescription").value = "";

  if (modal) {
    modal.classList.add("active");
    document.body.style.overflow = "hidden";
  }
}

function openModalForEdit(id) {
  const s = schedules.find(x => x.id === id);
  if (!s) return;

  editingId = id;

  document.getElementById("scheduleDate").value = s.date;
  document.getElementById("startTime").value = s.startTime;
  document.getElementById("endTime").value = s.endTime;
  document.getElementById("scheduleTitle").value = s.title;
  document.getElementById("scheduleDescription").value = s.description || "";

  const modal = document.getElementById("addBlockModal");
  if (modal) {
    modal.classList.add("active");
    document.body.style.overflow = "hidden";
  }
}

function closeModal() {
  const modal = document.getElementById("addBlockModal");
  if (modal) modal.classList.remove("active");
  document.body.style.overflow = "";
  editingId = null;
}

// -------------------------
// 追加/更新/削除
// -------------------------
function handleSubmit() {
  if (editingId === null) addSchedule();
  else updateSchedule(editingId);
}

function addSchedule() {
  const date = document.getElementById("scheduleDate").value;
  const startTime = document.getElementById("startTime").value;
  const endTime = document.getElementById("endTime").value;
  const title = document.getElementById("scheduleTitle").value.trim();
  const description = document.getElementById("scheduleDescription").value.trim();

  if (!title) {
    alert("タイトルを入力してください");
    return;
  }

  schedules.push({
    id: Date.now(),
    date, startTime, endTime, title, description
  });

  sortSchedules();
  renderSchedules();
  closeModal();
}

function updateSchedule(id) {
  const s = schedules.find(x => x.id === id);
  if (!s) return;

  s.date = document.getElementById("scheduleDate").value;
  s.startTime = document.getElementById("startTime").value;
  s.endTime = document.getElementById("endTime").value;
  s.title = document.getElementById("scheduleTitle").value.trim();
  s.description = document.getElementById("scheduleDescription").value.trim();

  if (!s.title) {
    alert("タイトルを入力してください");
    return;
  }

  sortSchedules();
  renderSchedules();
  closeModal();
}

function deleteSchedule(id) {
  if (!confirm("この予定を削除しますか？")) return;
  schedules = schedules.filter(x => x.id !== id);
  renderSchedules();
}

function sortSchedules() {
  schedules.sort((a, b) => (a.date !== b.date ? a.date.localeCompare(b.date) : a.startTime.localeCompare(b.startTime)));
}

// -------------------------
// 描画（更新・削除ボタンも出す）
// -------------------------
function renderSchedules() {
  if (!Array.isArray(window.dateList)) return;

  window.dateList.forEach((date, idx) => {
    const list = document.getElementById(`scheduleList-${idx}`);
    const empty = document.getElementById(`emptyState-${idx}`);
    if (!list || !empty) return;

    list.querySelectorAll(".schedule-item").forEach(el => el.remove());

    const daySchedules = schedules.filter(s => s.date === date);

    if (daySchedules.length === 0) {
      empty.style.display = "flex";
      return;
    }
    empty.style.display = "none";

    daySchedules.forEach(s => {
      const item = document.createElement("div");
      item.className = "schedule-item";
      item.innerHTML = `
        <div class="schedule-time">${escapeHtml(s.startTime)} 〜 ${escapeHtml(s.endTime)}</div>
        <div class="schedule-item-title">${escapeHtml(s.title)}</div>
        ${s.description ? `<div class="schedule-item-description">${escapeHtml(s.description)}</div>` : ""}
        <div class="schedule-actions">
          <button type="button" class="btn-edit">変更</button>
          <button type="button" class="btn-delete">削除</button>
        </div>
      `;

      item.querySelector(".btn-edit").addEventListener("click", () => openModalForEdit(s.id));
      item.querySelector(".btn-delete").addEventListener("click", () => deleteSchedule(s.id));

      list.appendChild(item);
    });
  });
}

function escapeHtml(text) {
  const div = document.createElement("div");
  div.textContent = text;
  return div.innerHTML;
}

// -------------------------
// DB保存（gson不要：フォームPOSTで配列送る）
// -------------------------
function submitToSaveServlet() {
  const travelId = window.travelId;

  if (!travelId || travelId <= 0) {
    alert("travelId が取れていません（create.jsp の request.getAttribute(\"TRAVEL_ID\") を確認）");
    return;
  }

  const form = document.createElement("form");
  form.method = "POST";
  form.action = "SaveScheduleServlet";

  addHidden(form, "travelId", String(travelId));

  schedules.forEach(s => {
    addHidden(form, "schedule_date", s.date);
    addHidden(form, "start_time", s.startTime);
    addHidden(form, "end_time", s.endTime);
    addHidden(form, "title", s.title);
    addHidden(form, "description", s.description || "");
  });

  document.body.appendChild(form);
  form.submit();
}

function addHidden(form, name, value) {
  const input = document.createElement("input");
  input.type = "hidden";
  input.name = name;
  input.value = value;
  form.appendChild(input);
}
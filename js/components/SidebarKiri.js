class SidebarKiri extends HTMLElement {
  connectedCallback() {
    this.className = "sidebar";
    this.innerHTML = `
      <div class="sb-logo">
        <div class="sb-logo-name">MuragamePOS</div>
        <div class="sb-logo-sub">Point of Sale</div>
      </div>
      <div class="sb-section">MENU</div>
      <div id="sbTransaksi" class="sb-item active" onclick="switchView('transaksi')">
        <div class="sb-dot" style="background:var(--color-text-info)"></div>Transaksi Baru
      </div>
      <div id="sbRiwayat" class="sb-item" onclick="switchView('riwayat')">
        <div class="sb-dot" style="background:var(--color-border-secondary)"></div>Riwayat Order
      </div>
      <div class="sb-section">MANAJEMEN</div>
      <div id="sbDataMenu" class="sb-item" onclick="switchView('datamenu')">
        <div class="sb-dot" style="background:var(--color-border-secondary)"></div>Data Menu
      </div>
      <div class="sb-spacer"></div>
      <div class="sb-section">SESSION</div>
      <div class="sb-item danger" onclick="logout()">
        <div class="sb-dot" style="background:var(--color-text-danger)"></div>Logout
      </div>
    `;
  }
}
customElements.define('sidebar-kiri', SidebarKiri);

// Navigation and session functions related to Sidebar
window.switchView = function(view) {
  var menuSectionEl = document.getElementById('menuSectionEl');
  var detailPesananEl = document.getElementById('detailPesananEl');
  
  if (view === 'transaksi') {
    activeView = 'transaksi';
    document.getElementById('sbTransaksi').classList.add('active');
    document.getElementById('sbRiwayat').classList.remove('active');
    document.getElementById('sbDataMenu').classList.remove('active');
    document.getElementById('sbTransaksi').querySelector('.sb-dot').style.backgroundColor = 'var(--color-text-info)';
    document.getElementById('sbRiwayat').querySelector('.sb-dot').style.backgroundColor = 'var(--color-border-secondary)';
    document.getElementById('sbDataMenu').querySelector('.sb-dot').style.backgroundColor = 'var(--color-border-secondary)';
    
    if (menuSectionEl) {
      menuSectionEl.style.gridColumn = '2';
      document.getElementById('menuViewSection').style.display = 'block';
      document.getElementById('historyView').style.display = 'none';
      document.getElementById('dataMenuView').style.display = 'none';
    }
    if (detailPesananEl) {
      detailPesananEl.style.display = 'flex';
      detailPesananEl.style.flexDirection = 'column';
    }
  } else if (view === 'riwayat') {
    activeView = 'riwayat';
    document.getElementById('sbTransaksi').classList.remove('active');
    document.getElementById('sbRiwayat').classList.add('active');
    document.getElementById('sbDataMenu').classList.remove('active');
    document.getElementById('sbTransaksi').querySelector('.sb-dot').style.backgroundColor = 'var(--color-border-secondary)';
    document.getElementById('sbRiwayat').querySelector('.sb-dot').style.backgroundColor = 'var(--color-text-info)';
    document.getElementById('sbDataMenu').querySelector('.sb-dot').style.backgroundColor = 'var(--color-border-secondary)';
    
    if (menuSectionEl) {
      menuSectionEl.style.gridColumn = '2 / 4';
      document.getElementById('menuViewSection').style.display = 'none';
      document.getElementById('historyView').style.display = 'block';
      document.getElementById('dataMenuView').style.display = 'none';
    }
    if (detailPesananEl) {
      detailPesananEl.style.display = 'none';
    }
    
    renderHistory();
  } else if (view === 'datamenu') {
    activeView = 'datamenu';
    document.getElementById('sbTransaksi').classList.remove('active');
    document.getElementById('sbRiwayat').classList.remove('active');
    document.getElementById('sbDataMenu').classList.add('active');
    document.getElementById('sbTransaksi').querySelector('.sb-dot').style.backgroundColor = 'var(--color-border-secondary)';
    document.getElementById('sbRiwayat').querySelector('.sb-dot').style.backgroundColor = 'var(--color-border-secondary)';
    document.getElementById('sbDataMenu').querySelector('.sb-dot').style.backgroundColor = 'var(--color-text-info)';
    
    if (menuSectionEl) {
      menuSectionEl.style.gridColumn = '2 / 4';
      document.getElementById('menuViewSection').style.display = 'none';
      document.getElementById('historyView').style.display = 'none';
      document.getElementById('dataMenuView').style.display = 'block';
    }
    if (detailPesananEl) {
      detailPesananEl.style.display = 'none';
    }
    
    renderDataMenuTable();
  }
};

window.logout = function() {
  if (window.javaApp) {
    window.javaApp.exitApp();
  } else {
    window.close();
  }
};

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

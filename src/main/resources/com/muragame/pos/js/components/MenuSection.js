class MenuSection extends HTMLElement {
  connectedCallback() {
    this.className = "main";
    this.innerHTML = `
      <div id="menuViewSection">
        <div class="tabs">
          <div class="tab active" onclick="filterMenu('semua',this)">Semua</div>
          <div class="tab" onclick="filterMenu('ramen',this)">Ramen</div>
          <div class="tab" onclick="filterMenu('minuman',this)">Minuman</div>
          <div class="tab" onclick="filterMenu('snack',this)">Snack</div>
        </div>

        <div class="sec-label">Pilih layanan</div>
        <div class="svc-row">
          <div class="svc sel" onclick="selectSvc(this,'Dine In')"><div class="svc-name">Dine In</div><div class="svc-price">+Rp 5.000</div></div>
          <div class="svc" onclick="selectSvc(this,'Take Away')"><div class="svc-name">Take Away</div><div class="svc-price">+Rp 2.000</div></div>
          <div class="svc" onclick="selectSvc(this,'Delivery')"><div class="svc-name">Delivery</div><div class="svc-price">+Rp 15.000</div></div>
        </div>

        <div class="sec-label">Pilih menu</div>
        <div class="menu-grid" id="menuGrid"></div>
      </div>

      <!-- History View (Hidden by default) -->
      <div id="historyView" style="display:none; overflow-y: auto;">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
          <div class="sec-label" style="margin-bottom:0;">Riwayat Order Hari Ini</div>
          <span id="historyCount" style="background:var(--color-background-info); color:var(--color-text-info); padding:2px 8px; border-radius:10px; font-size:10px; font-weight:500;">0 Transaksi</span>
        </div>
        <div style="background:var(--color-background-primary); border:0.5px solid var(--color-border-tertiary); border-radius:var(--border-radius-lg); overflow:hidden;">
          <table class="history-table">
            <thead>
              <tr>
                <th>ID Order</th>
                <th>Waktu</th>
                <th>Pelanggan</th>
                <th>Layanan</th>
                <th>Total Bersih</th>
                <th style="text-align:right;">Aksi</th>
              </tr>
            </thead>
            <tbody id="historyTableBody">
              <!-- populated dynamically -->
            </tbody>
          </table>
        </div>
      </div>
    `;
  }
}
customElements.define('menu-section', MenuSection);

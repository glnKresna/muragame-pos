class PopupKustomisasi extends HTMLElement {
  connectedCallback() {
    this.innerHTML = `
      <!-- Modal Kustomisasi Ramen -->
      <div id="customizationModal" class="modal" style="display:none;">
        <div class="modal-content" style="width: 400px;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:15px; border-bottom:0.5px solid var(--color-border-tertiary); padding-bottom:10px;">
            <h3 id="custModalTitle" style="font-size:13px; font-weight:600;">Kustomisasi Ramen</h3>
            <span class="close-btn" onclick="closeCustomizationModal()" style="cursor:pointer; font-size:18px; color:var(--color-text-secondary);">&times;</span>
          </div>
          
          <input type="hidden" id="custMenuId">
          
          <!-- Pedas Selection -->
          <div style="margin-bottom:12px;">
            <div class="sec-label">Tingkat Kepedasan (0 - 5)</div>
            <div style="display:flex; gap:6px; justify-content:space-between;">
              <button id="pedasBtn0" onclick="selectPedas(0)" class="quick-cash-btn pedas-btn active-pedas" style="width:45px; height:32px; font-weight:bold; font-size:12px;">0</button>
              <button id="pedasBtn1" onclick="selectPedas(1)" class="quick-cash-btn pedas-btn" style="width:45px; height:32px; font-weight:bold; font-size:12px;">1</button>
              <button id="pedasBtn2" onclick="selectPedas(2)" class="quick-cash-btn pedas-btn" style="width:45px; height:32px; font-weight:bold; font-size:12px;">2</button>
              <button id="pedasBtn3" onclick="selectPedas(3)" class="quick-cash-btn pedas-btn" style="width:45px; height:32px; font-weight:bold; font-size:12px;">3</button>
              <button id="pedasBtn4" onclick="selectPedas(4)" class="quick-cash-btn pedas-btn" style="width:45px; height:32px; font-weight:bold; font-size:12px;">4</button>
              <button id="pedasBtn5" onclick="selectPedas(5)" class="quick-cash-btn pedas-btn" style="width:45px; height:32px; font-weight:bold; font-size:12px;">5</button>
            </div>
          </div>
          
          <!-- Toppings Selection (Checkboxes) -->
          <div style="margin-bottom:12px;">
            <div class="sec-label">Pilih Topping Tambahan</div>
            <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px; background:var(--color-background-secondary); padding:10px; border-radius:var(--border-radius-md); border:0.5px solid var(--color-border-tertiary);">
              <label style="display:flex; align-items:center; gap:6px; cursor:pointer; font-size:12px;"><input type="checkbox" class="topping-cb" value="Chashu" checked> Chashu</label>
              <label style="display:flex; align-items:center; gap:6px; cursor:pointer; font-size:12px;"><input type="checkbox" class="topping-cb" value="Tamago" checked> Tamago</label>
              <label style="display:flex; align-items:center; gap:6px; cursor:pointer; font-size:12px;"><input type="checkbox" class="topping-cb" value="Nori" checked> Nori</label>
              <label style="display:flex; align-items:center; gap:6px; cursor:pointer; font-size:12px;"><input type="checkbox" class="topping-cb" value="Tempura"> Tempura</label>
              <label style="display:flex; align-items:center; gap:6px; cursor:pointer; font-size:12px;"><input type="checkbox" class="topping-cb" value="Narutomaki"> Narutomaki</label>
            </div>
          </div>
          
          <!-- Catatan -->
          <div style="margin-bottom:15px;">
            <div class="sec-label">Catatan Tambahan</div>
            <input type="text" id="custNotes" placeholder="Contoh: Mi agak lembek" style="width:100%; padding:8px 10px; border:0.5px solid var(--color-border-tertiary); border-radius:var(--border-radius-md); background:var(--color-background-secondary); color:var(--color-text-primary); font-size:12px;">
          </div>
          
          <button class="pay-btn" style="margin-top:5px;" onclick="confirmCustomization()">
            Tambahkan ke Keranjang
          </button>
        </div>
      </div>
    `;
  }
}
customElements.define('popup-kustomisasi', PopupKustomisasi);

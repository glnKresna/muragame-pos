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

// Functions related to Ramen Customization Modal
window.openCustomizationModal = function(m) {
  document.getElementById('custMenuId').value = m.id;
  document.getElementById('custModalTitle').textContent = 'Kustomisasi ' + m.name;
  
  selectedKuahVal = 'Shoyu';
  selectedPedasVal = 0;
  
  var kuahOpts = document.querySelectorAll('.kuah-opt');
  if (kuahOpts.length > 0) {
    kuahOpts.forEach(function(el) {
      el.classList.remove('sel');
    });
    kuahOpts[0].classList.add('sel');
  }
  
  selectPedas(0);
  
  var cbs = document.querySelectorAll('.topping-cb');
  cbs.forEach(function(cb) {
    if (cb.value === 'Chashu' || cb.value === 'Tamago' || cb.value === 'Nori') {
      cb.checked = true;
    } else {
      cb.checked = false;
    }
  });
  
  document.getElementById('custNotes').value = '';
  document.getElementById('customizationModal').style.display = 'flex';
};

window.closeCustomizationModal = function() {
  document.getElementById('customizationModal').style.display = 'none';
};

window.selectKuah = function(el, kuah) {
  document.querySelectorAll('.kuah-opt').forEach(function(o){o.classList.remove('sel')});
  el.classList.add('sel');
  selectedKuahVal = kuah;
};

window.selectPedas = function(level) {
  selectedPedasVal = level;
  document.querySelectorAll('.pedas-btn').forEach(function(btn, i) {
    if (i === level) {
      btn.classList.add('active-pedas');
    } else {
      btn.classList.remove('active-pedas');
    }
  });
};

window.confirmCustomization = function() {
  var menuId = document.getElementById('custMenuId').value;
  var menuObj = menus.find(function(m){return m.id === menuId});
  if (!menuObj) return;
  
  var toppings = [];
  document.querySelectorAll('.topping-cb').forEach(function(cb) {
    if (cb.checked) toppings.push(cb.value);
  });
  var toppingStr = toppings.join(', ');
  
  var notes = document.getElementById('custNotes').value;
  
  if (window.javaApp) {
    window.javaApp.addRamenWithCustomization(menuId, selectedPedasVal, toppingStr, notes);
    closeCustomizationModal();
    syncWithJava();
  } else {
    var kuah = "Original";
    var nameLower = menuObj.name.toLowerCase();
    if (nameLower.indexOf("shio") !== -1) kuah = "Shio";
    else if (nameLower.indexOf("shoyu") !== -1) kuah = "Shoyu";
    else if (nameLower.indexOf("miso") !== -1) kuah = "Miso";
    else if (nameLower.indexOf("paitan") !== -1) kuah = "Paitan";
    
    var detailParts = ["Kuah: " + kuah, "Pedas " + selectedPedasVal];
    if (toppingStr.trim() !== "") detailParts.push("Topping: " + toppingStr);
    if (notes.trim() !== "") detailParts.push("(" + notes + ")");
    var detailStr = detailParts.join(" · ");
    
    var found = false;
    for (var i = 0; i < mockCart.length; i++) {
      if (mockCart[i].menuId === menuId && mockCart[i].detail === detailStr) {
        mockCart[i].qty++;
        mockCart[i].lineTotal = mockCart[i].qty * mockCart[i].price;
        found = true;
        break;
      }
    }
    if (!found) {
      var invoiceId = "INV-MOCK-" + Date.now() + "-" + Math.floor(Math.random()*1000);
      mockCart.push({
        invoiceId: invoiceId,
        menuId: menuId,
        name: menuObj.name,
        price: menuObj.price,
        qty: 1,
        lineTotal: menuObj.price,
        detail: detailStr
      });
    }
    closeCustomizationModal();
    syncMockCart();
  }
};

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
          <div class="svc sel" onclick="selectSvc(this,'Dine In')"><div class="svc-name">Dine In</div><div class="svc-price">+Rp 0</div></div>
          <div class="svc" onclick="selectSvc(this,'Take Away')"><div class="svc-name">Take Away</div><div class="svc-price">+Rp 2.000</div></div>
          <div class="svc" onclick="selectSvc(this,'Delivery')"><div class="svc-name">Delivery</div><div class="svc-price">+Rp 15.000</div></div>
        </div>

        <div class="sec-label">Pilih menu</div>
        <div class="menu-grid" id="menuGrid"></div>
      </div>

      <!-- History View (Hidden by default) -->
      <div id="historyView" style="display:none; overflow-y: auto;">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
          <div class="sec-label" style="margin-bottom:0;">Riwayat Order</div>
          <span id="historyCount" style="background:var(--color-background-info); color:var(--color-text-info); padding:2px 8px; border-radius:10px; font-size:10px; font-weight:500;">0 Transaksi</span>
        </div>
        <div class="hist-filters">
          <div class="hist-filter active" onclick="filterHistory('today',this)">Hari Ini</div>
          <div class="hist-filter" onclick="filterHistory('week',this)">Minggu Ini</div>
          <div class="hist-filter" onclick="filterHistory('month',this)">Bulan Ini</div>
          <div class="hist-filter" onclick="filterHistory('year',this)">Tahun Ini</div>
          <div class="hist-filter" onclick="filterHistory('all',this)">Semua</div>
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

      <!-- Data Menu View (Hidden by default) -->
      <div id="dataMenuView" style="display:none; overflow-y: auto;">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
          <div class="sec-label" style="margin-bottom:0;">Manajemen Data Menu</div>
          <button class="quick-cash-btn" onclick="openMenuModal('add')" style="padding:6px 12px; font-weight:500; display:flex; align-items:center; gap:4px; background:var(--color-background-success); color:var(--color-text-success); border-color:var(--color-border-success);">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
            Tambah Menu Baru
          </button>
        </div>
        <div style="background:var(--color-background-primary); border:0.5px solid var(--color-border-tertiary); border-radius:var(--border-radius-lg); overflow:hidden;">
          <table class="history-table">
            <thead>
              <tr>
                <th>ID Menu</th>
                <th>Nama Menu</th>
                <th>Kategori</th>
                <th>Harga</th>
                <th style="text-align:right;">Aksi</th>
              </tr>
            </thead>
            <tbody id="dataMenuTableBody"></tbody>
          </table>
        </div>
      </div>
    `;
  }
}
customElements.define('menu-section', MenuSection);

// Functions related to Menu rendering, filtering and service options
window.renderMenus = function() {
  var g = document.getElementById('menuGrid');
  console.log("DEBUG: g =", g, "menus =", menus, "activeFilter =", activeFilter);
  if (!g) {
    console.error("DEBUG: menuGrid element not found!");
    return;
  }
  g.innerHTML = '';
  var filtered = activeFilter === 'semua' ? menus : menus.filter(function(m){return m.cat === activeFilter});
  filtered.forEach(function(m){
    var qtyCount = 0;
    Object.keys(cart).forEach(function(invoiceId) {
      if (cart[invoiceId].menu.id === m.id) {
        qtyCount += cart[invoiceId].qty;
      }
    });

    var inCart = qtyCount > 0;
    var d = document.createElement('div');
    d.className = 'mc' + (inCart ? ' added' : '');
    
    var badge = inCart ? '<div class="badge-count">' + qtyCount + '</div>' : '';
    d.innerHTML = '<div class="mc-name">' + m.name + '</div>' + 
                  '<div class="mc-cat">' + m.cat.charAt(0).toUpperCase() + m.cat.slice(1) + '</div>' + 
                  '<div class="mc-price">' + fmt(m.price) + '</div>' + 
                  badge;
    d.onclick = function(){ addItem(m) };
    g.appendChild(d);
  });
};

window.filterMenu = function(cat, el) {
  activeFilter = cat;
  document.querySelectorAll('.tab').forEach(function(t){t.classList.remove('active')});
  el.classList.add('active');
  renderMenus();
};

window.selectSvc = function(el, name) {
  document.querySelectorAll('.svc').forEach(function(s){s.classList.remove('sel')});
  el.classList.add('sel');
  if (window.javaApp) {
    window.javaApp.selectSvc(name);
    syncWithJava();
  } else {
    mockSvcName = name;
    if (name === 'Take Away') mockSvcPrice = 2000;
    else if (name === 'Delivery') mockSvcPrice = 15000;
    else mockSvcPrice = 0;
    syncMockCart();
  }
};

// Data Menu View & Actions
window.renderDataMenuTable = function() {
  var tbody = document.getElementById('dataMenuTableBody');
  if (!tbody) return;
  tbody.innerHTML = '';
  
  if (menus.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" style="padding:20px; text-align:center; color:var(--color-text-tertiary);">Belum ada data menu</td></tr>';
    return;
  }
  
  menus.forEach(function(m) {
    var tr = document.createElement('tr');
    tr.innerHTML = '<td style="padding:10px 14px; font-weight:500;">' + m.id + '</td>' +
                   '<td style="padding:10px 14px;">' + m.name + '</td>' +
                   '<td style="padding:10px 14px; text-transform:capitalize;">' + m.cat + '</td>' +
                   '<td style="padding:10px 14px; font-weight:600; color:var(--color-text-info);">' + fmt(m.price) + '</td>' +
                   '<td style="padding:10px 14px; text-align:right; display:flex; gap:6px; justify-content:flex-end;">' +
                     '<button class="quick-cash-btn" onclick="openMenuModal(\'edit\', \'' + m.id + '\')" style="padding:4px 8px;">Edit</button>' +
                     '<button class="quick-cash-btn" onclick="deleteMenuAction(\'' + m.id + '\')" style="padding:4px 8px; color:var(--color-text-danger); border-color:var(--color-background-danger);">Hapus</button>' +
                   '</td>';
    tbody.appendChild(tr);
  });
};

window.openMenuModal = function(action, id) {
  document.getElementById('menuActionType').value = action;
  
  if (action === 'add') {
    document.getElementById('menuModalTitle').textContent = 'Tambah Menu Baru';
    document.getElementById('menuIdInput').value = '';
    document.getElementById('menuIdInput').disabled = false;
    document.getElementById('menuNameInput').value = '';
    document.getElementById('menuCatInput').value = 'ramen';
    document.getElementById('menuPriceInput').value = '';
  } else {
    document.getElementById('menuModalTitle').textContent = 'Edit Menu';
    var m = menus.find(function(item){return item.id === id});
    if (!m) return;
    document.getElementById('menuIdInput').value = m.id;
    document.getElementById('menuIdInput').disabled = true;
    document.getElementById('menuNameInput').value = m.name;
    document.getElementById('menuCatInput').value = m.cat;
    document.getElementById('menuPriceInput').value = m.price;
  }
  
  document.getElementById('menuModal').style.display = 'flex';
};

window.closeMenuModal = function() {
  document.getElementById('menuModal').style.display = 'none';
};

window.saveMenu = function() {
  var action = document.getElementById('menuActionType').value;
  var id = document.getElementById('menuIdInput').value.trim();
  var name = document.getElementById('menuNameInput').value.trim();
  var cat = document.getElementById('menuCatInput').value;
  var price = parseFloat(document.getElementById('menuPriceInput').value) || 0;
  
  if (id === "" || name === "" || price <= 0) {
    alert("Semua field harus diisi dengan benar!");
    return;
  }
  
  if (action === 'add') {
    if (window.javaApp) {
      var resRaw = window.javaApp.addMenu(id, name, price, cat);
      var res = JSON.parse(resRaw);
      if (res.success) {
        var menusRaw = window.javaApp.getMenusJson();
        menus = JSON.parse(menusRaw);
        renderMenus();
        renderDataMenuTable();
        closeMenuModal();
      } else {
        alert(res.message);
      }
    } else {
      if (menus.some(function(m){return m.id.toLowerCase() === id.toLowerCase()})) {
        alert("ID Menu sudah digunakan!");
        return;
      }
      var tagClass = cat === 'ramen' ? 'tag-s' : 'tag-g';
      menus.push({
        id: id,
        name: name,
        cat: cat,
        price: price,
        tag: cat === 'ramen' ? 'Shoyu' : cat === 'minuman' ? 'Dingin' : 'Goreng',
        tagClass: tagClass,
        detail: cat === 'ramen' ? 'Chashu · Tamago · Nori' : 'Standard portion'
      });
      renderMenus();
      renderDataMenuTable();
      closeMenuModal();
    }
  } else {
    if (window.javaApp) {
      var resRaw = window.javaApp.editMenu(id, name, price, cat);
      var res = JSON.parse(resRaw);
      if (res.success) {
        var menusRaw = window.javaApp.getMenusJson();
        menus = JSON.parse(menusRaw);
        renderMenus();
        renderDataMenuTable();
        closeMenuModal();
      } else {
        alert(res.message);
      }
    } else {
      var m = menus.find(function(item){return item.id === id});
      if (m) {
        m.name = name;
        m.cat = cat;
        m.price = price;
      }
      renderMenus();
      renderDataMenuTable();
      closeMenuModal();
    }
  }
};

window.deleteMenuAction = function(id) {
  if (!confirm("Apakah Anda yakin ingin menghapus menu " + id + "?")) {
    return;
  }
  
  if (window.javaApp) {
    var resRaw = window.javaApp.deleteMenu(id);
    var res = JSON.parse(resRaw);
    if (res.success) {
      var menusRaw = window.javaApp.getMenusJson();
      menus = JSON.parse(menusRaw);
      renderMenus();
      renderDataMenuTable();
    } else {
      alert(res.message);
    }
  } else {
    var idx = menus.findIndex(function(m){return m.id === id});
    if (idx !== -1) {
      menus.splice(idx, 1);
    }
    renderMenus();
    renderDataMenuTable();
  }
};

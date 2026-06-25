class PopupPembayaran extends HTMLElement {
  connectedCallback() {
    this.innerHTML = `
      <!-- Modal Pembayaran -->
      <div id="paymentModal" class="modal" style="display:none;">
        <div class="modal-content">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:15px; border-bottom:0.5px solid var(--color-border-tertiary); padding-bottom:10px;">
            <h3 style="font-size:13px; font-weight:600;">Metode Pembayaran</h3>
            <span class="close-btn" onclick="closePaymentModal()" style="cursor:pointer; font-size:18px; color:var(--color-text-secondary);">&times;</span>
          </div>
          <div style="margin-bottom:15px;">
            <div style="font-size:11px; color:var(--color-text-secondary); margin-bottom:4px;">Total Tagihan</div>
            <div id="modalTotal" style="font-size:20px; font-weight:600; color:var(--color-text-info);">Rp 0</div>
          </div>
          <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; margin-bottom:15px;">
            <div>
              <label style="display:block; font-size:11px; color:var(--color-text-secondary); margin-bottom:6px;">Nama Pelanggan</label>
              <input type="text" id="custNameInput" value="Umum" placeholder="Contoh: Budi" style="width:100%; padding:8px 10px; border:0.5px solid var(--color-border-tertiary); border-radius:var(--border-radius-md); background:var(--color-background-secondary); color:var(--color-text-primary); font-size:13px;">
            </div>
            <div>
              <label style="display:block; font-size:11px; color:var(--color-text-secondary); margin-bottom:6px;">Diskon</label>
              <input type="number" id="manualDiscInput" value="0" min="0" max="100" oninput="updatePaymentTotals()" style="width:100%; padding:8px 10px; border:0.5px solid var(--color-border-tertiary); border-radius:var(--border-radius-md); background:var(--color-background-secondary); color:var(--color-text-primary); font-size:13px;">
            </div>
          </div>
          
          <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; margin-bottom:15px;">
            <div id="btnPayCash" class="pay-opt sel" onclick="selectPayMethod('CASH')">
              <div style="font-weight:500; font-size:12px;">TUNAI</div>
            </div>
            <div id="btnPayQris" class="pay-opt" onclick="selectPayMethod('QRIS')">
              <div style="font-weight:500; font-size:12px;">QRIS</div>
            </div>
          </div>
          
          <!-- Cash Input Section -->
          <div id="cashSection" style="margin-bottom:15px;">
            <label style="display:block; font-size:11px; color:var(--color-text-secondary); margin-bottom:6px;">Uang Diterima (Rp)</label>
            <input type="number" id="cashAmount" placeholder="Contoh: 100000" style="width:100%; padding:8px 10px; border:0.5px solid var(--color-border-tertiary); border-radius:var(--border-radius-md); background:var(--color-background-secondary); color:var(--color-text-primary); font-size:13px;" oninput="calcChange()">
            
            <!-- Quick Cash Options -->
            <div style="display:grid; grid-template-columns:repeat(3, 1fr); gap:6px; margin-top:8px;">
              <button class="quick-cash-btn" onclick="setQuickCash(50000)">50k</button>
              <button class="quick-cash-btn" onclick="setQuickCash(100000)">100k</button>
              <button class="quick-cash-btn" onclick="setQuickCash('pas')">Uang Pas</button>
            </div>
            
            <div style="margin-top:12px; display:flex; justify-content:space-between; font-size:12px;">
              <span>Kembalian</span>
              <span id="cashChange" style="font-weight:600; color:var(--color-text-success);">Rp 0</span>
            </div>
          </div>
          
          <!-- QRIS Section -->
          <div id="qrisSection" style="display:none; text-align:center; margin-bottom:15px; padding:10px; background:var(--color-background-secondary); border-radius:var(--border-radius-md);">
            <div style="font-size:11px; color:var(--color-text-secondary); margin-bottom:8px;">Silakan Scan QRIS MuragamePOS</div>
            <div style="margin: 0 auto; width: 220px; height: 220px; background: white; padding: 10px; display: flex; align-items: center; justify-content: center; border: 0.5px solid var(--color-border-tertiary); border-radius: 4px;">
              <img src="img/qris.png" style="width: 200px; height: 200px; object-fit: contain;">
            </div>
            <div style="font-size:10px; color:var(--color-text-tertiary); margin-top:8px;">Simulasi QRIS Aktif</div>
          </div>
          
          <button class="pay-btn" id="confirmPayBtn" style="margin-top:10px;" onclick="confirmPayment()">
            Selesaikan Transaksi
          </button>
        </div>
      </div>

      <!-- Modal Struk / Receipt -->
      <div id="receiptModal" class="modal" style="display:none;">
        <div class="receipt-content">
          <div style="text-align:center; border-bottom:1px dashed #333; padding-bottom:10px; margin-bottom:10px;">
            <h3 style="margin: 0; font-size:14px; font-weight:bold; letter-spacing:1px;">MURAGAME RESTO</h3>
            <div style="font-size:10px; margin-top:3px;">Ramen & Japanese Snacks</div>
            <div style="font-size:10px;">Purwokerto</div>
          </div>
          <div style="font-size:10px; margin-bottom:10px; line-height:1.4;">
            <div style="display:flex; justify-content:space-between;"><span>Order ID:</span><span id="rOrderId">ORD-XXXX</span></div>
            <div style="display:flex; justify-content:space-between;"><span>Waktu   :</span><span id="rDate">2026-06-08</span></div>
            <div style="display:flex; justify-content:space-between;"><span>Kasir   :</span><span>Ahmad Kasir</span></div>
            <div style="display:flex; justify-content:space-between;"><span>Pelanggan:</span><span id="rCustomer">Budi Santoso</span></div>
          </div>
          <div style="border-bottom:1px dashed #333; margin-bottom:10px;"></div>
          <div id="rItems" style="font-size:10px; margin-bottom:10px; line-height:1.4;">
            <!-- items dynamically added here -->
          </div>
          <div style="border-bottom:1px dashed #333; margin-bottom:10px;"></div>
          <div style="font-size:10px; line-height:1.4; margin-bottom:15px;">
            <div style="display:flex; justify-content:space-between;"><span>Subtotal:</span><span id="rSubtotal">Rp 0</span></div>
            <div style="display:flex; justify-content:space-between;"><span>Layanan :</span><span id="rSvc">Rp 0</span></div>
            <div style="display:flex; justify-content:space-between;"><span>Diskon  :</span><span id="rDisc">Rp 0</span></div>
            <div style="display:flex; justify-content:space-between; font-weight:bold;"><span>Total   :</span><span id="rTotal">Rp 0</span></div>
            <div style="border-bottom:1px dashed #333; margin:5px 0;"></div>
            <div style="display:flex; justify-content:space-between;"><span>Metode  :</span><span id="rMethod">CASH</span></div>
            <div style="display:flex; justify-content:space-between;"><span>Bayar   :</span><span id="rPaid">Rp 0</span></div>
            <div style="display:flex; justify-content:space-between; font-weight:bold;"><span>Kembalian:</span><span id="rChange">Rp 0</span></div>
          </div>
          <div style="text-align:center; border-top:1px dashed #333; padding-top:10px; font-size:10px; line-height:1.3;">
            <div style="font-weight:bold;">Terima Kasih Atas Kunjungan Anda</div>
            <div>Arigatou Gozaimasu!</div>
            <button onclick="closeReceiptModal()" style="margin-top:15px; font-family:sans-serif; background:var(--color-text-primary); color:#fff; border:none; padding:6px 12px; border-radius:4px; cursor:pointer; font-size:11px; font-weight:500;">Tutup Struk</button>
          </div>
        </div>
      </div>
    `;
  }
}
customElements.define('popup-pembayaran', PopupPembayaran);

// Payment transaction variables
var originalTotalValue = 0;
var discountedTotalValue = 0;
var activeHistoryFilter = 'today';

// Payment processing functions
window.updatePaymentTotals = function () {
  var discPercentage = parseFloat(document.getElementById('manualDiscInput').value) || 0;
  if (discPercentage < 0) discPercentage = 0;
  if (discPercentage > 100) {
    discPercentage = 100;
    document.getElementById('manualDiscInput').value = 100;
  }

  var subtotalText = document.getElementById('fSubtotal') ? document.getElementById('fSubtotal').textContent : "0";
  var subtotalVal = parseFloat(subtotalText.replace(/[^0-9]/g, '')) || 0;
  var discRupiah = Math.round(subtotalVal * (discPercentage / 100));

  discountedTotalValue = Math.max(0, originalTotalValue - discRupiah);
  document.getElementById('modalTotal').textContent = fmt(discountedTotalValue);

  calcChange();
};

window.goPayment = function () {
  originalTotalValue = currentTotalValue;
  discountedTotalValue = currentTotalValue;

  document.getElementById('custNameInput').value = 'Umum';
  document.getElementById('manualDiscInput').value = 0;

  document.getElementById('cashAmount').value = '';
  document.getElementById('cashChange').textContent = 'Rp 0';
  document.getElementById('confirmPayBtn').disabled = activePayMethod === 'CASH';

  selectPayMethod('CASH');
  updatePaymentTotals();

  document.getElementById('paymentModal').style.display = 'flex';
};

window.closePaymentModal = function () {
  document.getElementById('paymentModal').style.display = 'none';
};

window.selectPayMethod = function (method) {
  activePayMethod = method;
  document.querySelectorAll('.pay-opt').forEach(function (o) { o.classList.remove('sel') });

  if (method === 'CASH') {
    document.getElementById('btnPayCash').classList.add('sel');
    document.getElementById('cashSection').style.display = 'block';
    document.getElementById('qrisSection').style.display = 'none';
    calcChange();
  } else {
    document.getElementById('btnPayQris').classList.add('sel');
    document.getElementById('cashSection').style.display = 'none';
    document.getElementById('qrisSection').style.display = 'block';
    document.getElementById('confirmPayBtn').disabled = false;
  }
};

window.setQuickCash = function (val) {
  var cashInput = document.getElementById('cashAmount');
  if (val === 'pas') {
    cashInput.value = discountedTotalValue;
  } else {
    cashInput.value = val;
  }
  calcChange();
};

window.calcChange = function () {
  if (activePayMethod !== 'CASH') return;

  var cashReceived = parseFloat(document.getElementById('cashAmount').value) || 0;
  var change = cashReceived - discountedTotalValue;

  var changeEl = document.getElementById('cashChange');
  if (change < 0) {
    changeEl.textContent = 'Kurang ' + fmt(Math.abs(change));
    changeEl.style.color = 'var(--color-text-danger)';
    document.getElementById('confirmPayBtn').disabled = true;
  } else {
    changeEl.textContent = fmt(change);
    changeEl.style.color = 'var(--color-text-success)';
    document.getElementById('confirmPayBtn').disabled = false;
  }
};

window.confirmPayment = function () {
  var custName = document.getElementById('custNameInput').value.trim();
  if (custName === "") {
    alert("Nama pelanggan tidak boleh kosong!");
    return;
  }

  if (custName.split(/\s+/).length > 1) {
    alert("Nama pelanggan hanya boleh satu kata saja!");
    return;
  }

  var discountAmount = parseFloat(document.getElementById('manualDiscInput').value) || 0;
  if (discountAmount < 0) {
    alert("Diskon tidak boleh negatif!");
    return;
  }

  var cashReceived = 0;
  if (activePayMethod === 'CASH') {
    cashReceived = parseFloat(document.getElementById('cashAmount').value) || 0;
    if (cashReceived < discountedTotalValue) {
      alert("Uang yang diterima kurang!");
      return;
    }
  }

  if (window.javaApp) {
    var responseRaw = window.javaApp.processPayment(activePayMethod, cashReceived, custName, discountAmount);
    var response = JSON.parse(responseRaw);

    if (response.success) {
      closePaymentModal();
      showReceipt(response);
      syncWithJava();
    } else {
      alert(response.message || "Pembayaran gagal!");
    }
  } else {
    var subtotal = 0;
    mockCart.forEach(function (item) {
      subtotal += item.price * item.qty;
    });
    var discRupiah = Math.round(subtotal * (discountAmount / 100));
    var total = subtotal > 0 ? (subtotal + mockSvcPrice - discRupiah) : 0;
    if (total < 0) total = 0;

    var change = activePayMethod === 'CASH' ? (cashReceived - total) : 0;

    var dateStr = new Date().toISOString().replace('T', ' ').substring(0, 19);
    var orderId = "ORD-MOCK-" + (mockHistory.length + 101);

    var txItems = [];
    mockCart.forEach(function (item) {
      txItems.push({
        name: item.name,
        qty: item.qty,
        total: item.lineTotal
      });
    });

    var response = {
      success: true,
      orderId: orderId,
      date: dateStr,
      customerName: custName,
      subtotal: subtotal,
      service: mockSvcPrice,
      discount: discRupiah,
      total: total,
      method: activePayMethod,
      paid: activePayMethod === 'CASH' ? cashReceived : total,
      change: change,
      items: txItems
    };

    mockHistory.push({
      orderId: orderId,
      date: dateStr,
      customerName: custName,
      subtotal: subtotal,
      service: mockSvcPrice,
      serviceName: mockSvcName,
      discount: discRupiah,
      total: total,
      method: activePayMethod,
      paid: activePayMethod === 'CASH' ? cashReceived : total,
      change: change,
      items: txItems
    });

    mockCart = [];
    closePaymentModal();
    showReceipt(response);
    syncMockCart();
  }
};

window.showReceipt = function (data) {
  document.getElementById('rOrderId').textContent = data.orderId;
  document.getElementById('rDate').textContent = data.date;
  document.getElementById('rCustomer').textContent = data.customerName;
  document.getElementById('rSubtotal').textContent = fmt(data.subtotal);
  document.getElementById('rSvc').textContent = fmt(data.service);
  document.getElementById('rDisc').textContent = '– ' + fmt(data.discount);
  document.getElementById('rTotal').textContent = fmt(data.total);
  document.getElementById('rMethod').textContent = data.method;
  document.getElementById('rPaid').textContent = fmt(data.paid);
  document.getElementById('rChange').textContent = fmt(data.change);

  var itemsWrap = document.getElementById('rItems');
  if (!itemsWrap) return;
  itemsWrap.innerHTML = '';
  data.items.forEach(function (item) {
    var line = document.createElement('div');
    line.style.display = 'flex';
    line.style.justifyContent = 'space-between';
    line.innerHTML = '<span>' + item.name + ' x' + item.qty + '</span>' +
      '<span>' + fmt(item.total) + '</span>';
    itemsWrap.appendChild(line);
  });

  document.getElementById('receiptModal').style.display = 'flex';
};

window.closeReceiptModal = function () {
  document.getElementById('receiptModal').style.display = 'none';
};

window.filterHistory = function (filter, el) {
  activeHistoryFilter = filter;
  document.querySelectorAll('.hist-filter').forEach(function (f) { f.classList.remove('active') });
  if (el) el.classList.add('active');
  renderHistory();
};

window.renderHistory = function () {
  if (window.javaApp) {
    // Load from database with filter
    var dbHistoryRaw = window.javaApp.getHistoryFromDB(activeHistoryFilter);
    var dbHistory = JSON.parse(dbHistoryRaw);

    // Also include current session history (not yet in DB if just paid)
    var sessionRaw = window.javaApp.getHistoryJson();
    var sessionHistory = JSON.parse(sessionRaw);

    // Merge: DB first, then session items not already in DB
    var dbIds = {};
    dbHistory.forEach(function (t) { dbIds[t.orderId] = true; });
    pastTransactionsList = dbHistory.slice();
    sessionHistory.forEach(function (t) {
      if (!dbIds[t.orderId]) pastTransactionsList.push(t);
    });
  } else {
    pastTransactionsList = mockHistory;
  }

  var countEl = document.getElementById('historyCount');
  if (countEl) countEl.textContent = pastTransactionsList.length + ' Transaksi';

  var tbody = document.getElementById('historyTableBody');
  if (!tbody) return;
  tbody.innerHTML = '';

  if (pastTransactionsList.length === 0) {
    tbody.innerHTML = '<tr><td colspan="6" style="padding:20px; text-align:center; color:var(--color-text-tertiary);">Belum ada riwayat transaksi</td></tr>';
    return;
  }

  pastTransactionsList.forEach(function (tx, index) {
    var timeOnly = tx.date.split(' ')[1] || tx.date;
    var tr = document.createElement('tr');
    tr.innerHTML = '<td style="padding:10px 14px; font-weight:500;">' + tx.orderId + '</td>' +
      '<td style="padding:10px 14px; color:var(--color-text-secondary);">' + timeOnly + '</td>' +
      '<td style="padding:10px 14px;">' + tx.customerName + '</td>' +
      '<td style="padding:10px 14px; color:var(--color-text-secondary);">' + tx.serviceName + '</td>' +
      '<td style="padding:10px 14px; font-weight:600; color:var(--color-text-info);">' + fmt(tx.total) + '</td>' +
      '<td style="padding:10px 14px; text-align:right;">' +
      '<button class="quick-cash-btn" onclick="reprintStruk(' + index + ')" style="padding:4px 8px;">Lihat Struk</button>' +
      '</td>';
    tbody.appendChild(tr);
  });
};

window.reprintStruk = function (index) {
  var tx = pastTransactionsList[index];
  if (!tx) return;

  var data = {
    orderId: tx.orderId,
    date: tx.date,
    customerName: tx.customerName,
    subtotal: tx.subtotal,
    service: tx.service,
    discount: tx.discount,
    total: tx.total,
    method: tx.method || 'CASH',
    paid: tx.paid || tx.total,
    change: tx.change || 0,
    items: tx.items
  };

  showReceipt(data);
};

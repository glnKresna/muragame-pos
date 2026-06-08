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
            <div style="margin: 0 auto; width: 120px; height: 120px; background: white; padding: 10px; display: flex; align-items: center; justify-content: center; border: 0.5px solid var(--color-border-tertiary); border-radius: 4px;">
              <svg width="100" height="100" viewBox="0 0 29 29" fill="black">
                <path d="M0 0h9v9H0zm1 1v7h7V1zm8 0h3v1H9zm0 2h1v3H9zm1-1h1v1h-1zm1 2h1v1h-1zm0 2h1v1h-1zm1 1h1v1h-1zm-1-7h1v1h-1zm0 2h1v1h-1zm2 1h1v2h-1zm1-3v1h1V3zm2 0h1v1h-1zm-1 3v1h1V6zm3-3h1v1h-1zm-1 4h1v1h-1zm3-4h6v6h-6zm1 1v4h4V10zm-9 6h1v1H9zm1 1h1v1h-1zm0-2h1v1h-1zm2 1h1v1h-1zm-1 2h1v1h-1zm2-2h1v1h-1zm1 1h1v1h-1zm1-2h1v1h-1zm1 1h1v1h-1zm-3 2h1v2h-1zm1 1v1h1v-1zm1-3h1v1h-1zm2 1h1v2h-1zm1 1v1h1v-1zm1-3v1h1v-1zm0 2h1v1h-1zm-13 4h9v9H0zm1 1v7h7v-7zm12-4h1v1h-1zm1 1v1h1v-1zm-2 2h1v1h-1zm3-1h1v2h-1zm-2 2h1v1h-1zm2 1h1v1h-1zm-2 1h1v1h-1zm1-3v1h1v-1zm0 4v1h1v-1zm3-3h1v1h-1zm0 2h1v1h-1zm1-1h1v1h-1zm1-2h1v1h-1zm0 2h1v1h-1zm1 1h1v1h-1zm-1 2h1v1h-1zm1 1h1v1h-1zm-5-10h1v1h-1zm2 1h1v1h-1zm-1 2h1v1h-1zm2-1h1v1h-1z"/>
              </svg>
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
            <div style="font-size:10px;">Sleman, Yogyakarta</div>
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

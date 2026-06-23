class DetailPesanan extends HTMLElement {
  connectedCallback() {
    this.className = "cart";
    this.innerHTML = `
      <div class="cart-top">
        <div class="cart-title">Detail pesanan</div>
        <div class="cust-row">
          <div class="cust-av" id="custAvLabel">UM</div>
          <div>
            <div class="cust-name" id="custNameLabel">Umum</div>
            <div class="cust-type" id="custTypeLabel">Regular Customer</div>
          </div>
        </div>
      </div>
      <div class="cart-items" id="cartItems">
        <div class="cart-empty" id="cartEmpty">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 01-8 0"/></svg>
          <span style="font-size:12px">Belum ada item</span>
        </div>
      </div>
      <div class="cart-footer">
        <div class="calc"><span>Sub total</span><span id="fSubtotal">Rp 0</span></div>
        <div class="calc"><span id="fSvcLabel">Layanan (Dine In)</span><span id="fSvc">Rp 5.000</span></div>
        <div class="calc disc"><span>Diskon member (10%)</span><span id="fDisc">– Rp 0</span></div>
        <div class="calc total"><span>Total bersih</span><span id="fTotal">Rp 0</span></div>
        <button class="pay-btn" id="payBtn" disabled onclick="goPayment()">
          <svg width="13" height="13" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"><rect x="1" y="4" width="14" height="10" rx="1.5"/><path d="M1 8h14M5 12h2"/></svg>
          Proses pembayaran
        </button>
      </div>
    `;
  }
}
customElements.define('detail-pesanan', DetailPesanan);

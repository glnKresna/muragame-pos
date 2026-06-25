var menus = [];
var cart = {};
var activeFilter = 'semua';
var activePayMethod = 'CASH';
var currentTotalValue = 0;
var activeView = 'transaksi';

var selectedKuahVal = 'Shoyu';
var selectedPedasVal = 0;
var pastTransactionsList = [];

// Mock system variables
var mockCart = [];
var mockSvcName = "Dine In";
var mockSvcPrice = 0;
var mockCustomerName = "Umum";
var mockCustomerType = "Regular Customer";
var mockDiscountRate = 0.00;
var mockHistory = [];

var isInitialized = false;

function fmt(n){
  return 'Rp ' + Math.round(n).toLocaleString('id-ID');
}

// Java integration loader / Mock initializer
function initApp() {
  if (isInitialized) return;
  isInitialized = true;
  
  if (window.javaApp) {
    try {
      // 1. Fetch menus from Java
      var menusRaw = window.javaApp.getMenusJson();
      menus = JSON.parse(menusRaw);
      
      // 2. Fetch shift & date info
      var h = new Date().getHours();
      var s = h>=6&&h<14?'Shift Pagi':h>=14&&h<21?'Shift Siang':'Shift Malam';
      document.getElementById('shiftLabel').textContent = s;
      document.getElementById('orderId').textContent = window.javaApp.getOrderId();
      
      // 3. Sync and render
      syncWithJava();
    } catch(err) {
      console.error("Java integration error: ", err);
    }
  } else {
    console.warn("window.javaApp is not injected yet! Loading mock data for development testing.");
    // Populate mock menus matching JavaBridge.java
    menus = [
      {"id":"M001","name":"Shio Tori Ramen","cat":"ramen","price":38000,"tag":"Shio","tagClass":"tag-s","detail":"Chashu · Tamago · Nori"},
      {"id":"M002","name":"Shoyu Tori Ramen","cat":"ramen","price":38000,"tag":"Shoyu","tagClass":"tag-s","detail":"Chashu · Tamago · Nori"},
      {"id":"M003","name":"Miso Tori Ramen","cat":"ramen","price":38000,"tag":"Miso","tagClass":"tag-g","detail":"Chashu · Tamago · Nori"},
      {"id":"M004","name":"Paitan Tori Ramen","cat":"ramen","price":38000,"tag":"Paitan","tagClass":"tag-r","detail":"Chashu · Tamago · Nori"},
      {"id":"M005","name":"Shio Beef Ramen","cat":"ramen","price":45000,"tag":"Shio","tagClass":"tag-s","detail":"Beef Slices · Tamago · Nori"},
      {"id":"M006","name":"Shoyu Beef Ramen","cat":"ramen","price":45000,"tag":"Shoyu","tagClass":"tag-s","detail":"Beef Slices · Tamago · Nori"},
      {"id":"M007","name":"Miso Beef Ramen","cat":"ramen","price":45000,"tag":"Miso","tagClass":"tag-g","detail":"Beef Slices · Tamago · Nori"},
      {"id":"M008","name":"Paitan Beef Ramen","cat":"ramen","price":45000,"tag":"Paitan","tagClass":"tag-r","detail":"Beef Slices · Tamago · Nori"},
      {"id":"M009","name":"Shio Tempura Ramen","cat":"ramen","price":42000,"tag":"Shio","tagClass":"tag-s","detail":"Tempura · Tamago · Nori"},
      {"id":"M010","name":"Shoyu Tempura Ramen","cat":"ramen","price":42000,"tag":"Shoyu","tagClass":"tag-s","detail":"Tempura · Tamago · Nori"},
      {"id":"M011","name":"Miso Tempura Ramen","cat":"ramen","price":42000,"tag":"Miso","tagClass":"tag-g","detail":"Tempura · Tamago · Nori"},
      {"id":"M012","name":"Paitan Tempura Ramen","cat":"ramen","price":42000,"tag":"Paitan","tagClass":"tag-r","detail":"Tempura · Tamago · Nori"},
      {"id":"M013","name":"Es Matcha Latte","cat":"minuman","price":22000,"tag":"Dingin","tagClass":"tag-g","detail":"Less sugar available"},
      {"id":"M014","name":"Es Teh Tarik","cat":"minuman","price":15000,"tag":"Dingin","tagClass":"tag-g","detail":"Creamy blend"},
      {"id":"M015","name":"Gyoza (6 pcs)","cat":"snack","price":28000,"tag":"Goreng","tagClass":"tag-s","detail":"Pork Filling"},
      {"id":"M016","name":"Karaage","cat":"snack","price":25000,"tag":"Crispy","tagClass":"tag-s","detail":"Chicken · Mayo sauce"},
      {"id":"M017","name":"Takoyaki (6)","cat":"snack","price":23000,"tag":"Hangat","tagClass":"tag-s","detail":"Octopus · Bonito"}
    ];
    var h = new Date().getHours();
    var s = h>=6&&h<14?'Shift Pagi':h>=14&&h<21?'Shift Siang':'Shift Malam';
    document.getElementById('shiftLabel').textContent = s;
    document.getElementById('orderId').textContent = "ORD-MOCK-100";
    
    syncMockCart();
  }
}

// Dom load event fallback
window.addEventListener('DOMContentLoaded', function() {
  setTimeout(initApp, 100);
});

function syncWithJava() {
  if (!window.javaApp) {
    syncMockCart();
    return;
  }
  
  var summaryRaw = window.javaApp.getOrderSummaryJson();
  var summary = JSON.parse(summaryRaw);
  
  document.getElementById('custNameLabel').textContent = summary.customerName;
  document.getElementById('custTypeLabel').textContent = summary.customerType;
  var av = summary.customerName ? summary.customerName.substring(0, 2).toUpperCase() : "UM";
  document.getElementById('custAvLabel').textContent = av;
  
  document.getElementById('orderId').textContent = window.javaApp.getOrderId();
  
  cart = {};
  summary.items.forEach(function(item) {
    cart[item.invoiceId] = {
      menu: { id: item.menuId, name: item.name, price: item.price },
      qty: item.qty
    };
  });
  
  renderMenus();
  
  var wrap = document.getElementById('cartItems');
  if (summary.items.length === 0) {
    wrap.innerHTML = '<div class="cart-empty" id="cartEmpty">' + 
                     '<svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 01-8 0"/></svg>' + 
                     '<span style="font-size:12px">Belum ada item</span>' + 
                     '</div>';
    document.getElementById('payBtn').disabled = true;
  } else {
    wrap.innerHTML = '';
    summary.items.forEach(function(item) {
      var d = document.createElement('div');
      d.className = 'ci';
      d.innerHTML = '<div class="ci-info">' + 
                      '<div class="ci-name">' + item.name + '</div>' + 
                      '<div class="ci-detail">' + item.detail + '</div>' + 
                    '</div>' + 
                    '<div class="qty">' + 
                      '<div class="qb" onclick="changeQty(\'' + item.invoiceId + '\', -1)">−</div>' + 
                      '<div class="qn">' + item.qty + '</div>' + 
                      '<div class="qb" onclick="changeQty(\'' + item.invoiceId + '\', 1)">+</div>' + 
                    '</div>' + 
                    '<div class="ci-price">' + fmt(item.lineTotal) + '</div>';
      wrap.appendChild(d);
    });
    document.getElementById('payBtn').disabled = false;
  }
  
  document.getElementById('fSubtotal').textContent = fmt(summary.subtotal);
  document.getElementById('fSvcLabel').textContent = 'Layanan (' + summary.svcName + ')';
  document.getElementById('fSvc').textContent = fmt(summary.svcPrice);
  document.getElementById('fDisc').textContent = '– ' + fmt(summary.discount);
  document.getElementById('fTotal').textContent = fmt(summary.total);
  
  currentTotalValue = summary.total;
  
  if (activeView === 'riwayat') {
    renderHistory();
  }
}

function syncMockCart() {
  var subtotal = 0;
  mockCart.forEach(function(item) {
    subtotal += item.price * item.qty;
  });
  
  var discount = subtotal * mockDiscountRate;
  var total = subtotal > 0 ? (subtotal + mockSvcPrice - discount) : 0;
  if (subtotal <= 0) {
    discount = 0;
  }
  
  document.getElementById('orderId').textContent = "ORD-MOCK-" + (mockHistory.length + 100);
  
  cart = {};
  mockCart.forEach(function(item) {
    cart[item.invoiceId] = {
      menu: { id: item.menuId, name: item.name, price: item.price },
      qty: item.qty
    };
  });
  
  renderMenus();
  
  var wrap = document.getElementById('cartItems');
  if (mockCart.length === 0) {
    wrap.innerHTML = '<div class="cart-empty" id="cartEmpty">' + 
                     '<svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 01-8 0"/></svg>' + 
                     '<span style="font-size:12px">Belum ada item</span>' + 
                     '</div>';
    document.getElementById('payBtn').disabled = true;
  } else {
    wrap.innerHTML = '';
    mockCart.forEach(function(item) {
      var d = document.createElement('div');
      d.className = 'ci';
      d.innerHTML = '<div class="ci-info">' + 
                      '<div class="ci-name">' + item.name + '</div>' + 
                      '<div class="ci-detail">' + item.detail + '</div>' + 
                    '</div>' + 
                    '<div class="qty">' + 
                      '<div class="qb" onclick="changeQty(\'' + item.invoiceId + '\', -1)">−</div>' + 
                      '<div class="qn">' + item.qty + '</div>' + 
                      '<div class="qb" onclick="changeQty(\'' + item.invoiceId + '\', 1)">+</div>' + 
                    '</div>' + 
                    '<div class="ci-price">' + fmt(item.lineTotal) + '</div>';
      wrap.appendChild(d);
    });
    document.getElementById('payBtn').disabled = false;
  }
  
  document.getElementById('fSubtotal').textContent = fmt(subtotal);
  document.getElementById('fSvcLabel').textContent = 'Layanan (' + mockSvcName + ')';
  document.getElementById('fSvc').textContent = fmt(mockSvcPrice);
  document.getElementById('fDisc').textContent = '– ' + fmt(discount);
  document.getElementById('fTotal').textContent = fmt(total);
  
  currentTotalValue = total;
  
  if (activeView === 'riwayat') {
    renderHistory();
  }
}

// === Click-outside-to-close untuk semua modal ===
document.addEventListener('click', function(e) {
  // Kustomisasi modal
  var custModal = document.getElementById('customizationModal');
  if (custModal && custModal.style.display === 'flex' && e.target === custModal) {
    closeCustomizationModal();
  }
  // Payment modal
  var payModal = document.getElementById('paymentModal');
  if (payModal && payModal.style.display === 'flex' && e.target === payModal) {
    closePaymentModal();
  }
  // Receipt modal
  var rcptModal = document.getElementById('receiptModal');
  if (rcptModal && rcptModal.style.display === 'flex' && e.target === rcptModal) {
    closeReceiptModal();
  }
  // Menu modal
  var menuModal = document.getElementById('menuModal');
  if (menuModal && menuModal.style.display === 'flex' && e.target === menuModal) {
    closeMenuModal();
  }
});

// === ESC key untuk tutup modal ===
document.addEventListener('keydown', function(e) {
  if (e.key === 'Escape') {
    var custModal = document.getElementById('customizationModal');
    if (custModal && custModal.style.display === 'flex') {
      closeCustomizationModal();
      return;
    }
    var payModal = document.getElementById('paymentModal');
    if (payModal && payModal.style.display === 'flex') {
      closePaymentModal();
      return;
    }
    var rcptModal = document.getElementById('receiptModal');
    if (rcptModal && rcptModal.style.display === 'flex') {
      closeReceiptModal();
      return;
    }
    var menuModal = document.getElementById('menuModal');
    if (menuModal && menuModal.style.display === 'flex') {
      closeMenuModal();
      return;
    }
  }
});

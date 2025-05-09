// 쿼리 파라미터 값이 결제 요청할 때 보낸 데이터와 동일한지 반드시 확인하세요.
    // 클라이언트에서 결제 금액을 조작하는 행위를 방지할 수 있습니다.
    const urlParams = new URLSearchParams(window.location.search);
    const paymentKey = urlParams.get("paymentKey");
    const orderId = urlParams.get("orderId");
    const amount = urlParams.get("amount");

    async function confirm() {
      const requestData = {
        paymentKey: paymentKey,
        orderId: orderId,
        amount: amount,
      };

      const response = await fetch("http://localhost:8080/api/v1/orders/confirm", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(requestData),
      });

      const json = await response.json();

      if (!response.ok) {
        // 결제 실패 비즈니스 로직을 구현하세요.
        console.log(json);
        window.location.href = `/fail?message=${json.message}&code=${json.code}`;
      }


      // 결제 성공 비즈니스 로직을 구현하세요.
      console.log(json);
      setTimeout(() => {
          window.location.href = "/";
      }, 5000); // 5초 후 리디렉션

    }
    confirm();

    const paymentKeyElement = document.getElementById("paymentKey");
    const orderIdElement = document.getElementById("orderId");
    const amountElement = document.getElementById("amount");

    orderIdElement.textContent = "주문번호: " + orderId;
    amountElement.textContent = "결제 금액: " + amount;
    paymentKeyElement.textContent = "paymentKey: " + paymentKey;

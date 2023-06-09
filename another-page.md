## INFO WEEK1 AND WEEK2


<html>
<head>
  <style>
    .panel {
      display: none;
      background-color: #f1f1f1;
      padding: 10px;
      margin-top: 10px;
    }
  </style>
</head>
<body>
  <button onclick="showSoonText()">MicroBetaGUIImportAction</button>
  <div class="panel" id="soonPanel">
    <p>SOON tm</p>
  </div>

  <script>
    function showSoonText() {
      var panel = document.getElementById("soonPanel");
      if (panel.style.display === "none") {
        panel.style.display = "block";
      } else {
        panel.style.display = "none";
      }
    }
  </script>
</body>
</html>




[back](./)

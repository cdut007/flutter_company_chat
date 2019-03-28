import 'package:flutter/material.dart';
import 'package:flutter_app/util/GlobalConfig.dart';
import 'package:flutter_app/vcard/ApplyVcardPage.dart';
import 'package:flutter/services.dart';
import 'package:qr_code_scanner/qr_code_scanner.dart';

class QrCodeScanPage extends StatefulWidget{
  @override
  QrCodeScanPageState createState() => new QrCodeScanPageState();
}

class QrCodeScanPageState extends State<QrCodeScanPage> {
  final GlobalKey qrKey = GlobalKey(debugLabel: 'QR');
  var qrText = "";
  void _onQRViewCreated(QRViewController controller) {
    final channel = controller.channel;
    controller.init(qrKey);
    channel.setMethodCallHandler((MethodCall call) async {
      switch (call.method) {
        case "onRecognizeQR":
          dynamic arguments = call.arguments;

          Navigator.pushReplacement(context, new MaterialPageRoute(builder: (context)=> new ApplyVcardPage(key:Key('applyVcard'),applyUrl: qrText = arguments.toString())));

      }
    });
  }
  @override
  Widget build(BuildContext context) {
    return  new Scaffold(
          appBar: new AppBar(
            title: new Text("扫一扫", style: new TextStyle(color: Colors.white)),
            iconTheme: new IconThemeData(color: Colors.white),
          ),
          body: Column(
            children: <Widget>[
              Expanded(
                child: QRView(
                  key: qrKey,
                  onQRViewCreated: _onQRViewCreated,
                ),
                flex: 4,
              ),
            ],
          ),
        );
  }
}
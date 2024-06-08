import 'dart:convert';
import 'dart:io';
import 'dart:math';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';
import 'package:screenshot/screenshot.dart';
import 'package:web_socket_channel/io.dart';

// Access appData globaly with:
// AppData appData = Provider.of<AppData>(context);
// AppData appData = Provider.of<AppData>(context, listen: false)

enum ConnectionStatus {
  disconnected,
  disconnecting,
  connecting,
  connected,
  result,
}

class AppData with ChangeNotifier {
  Random r = new Random();
  String ip = "0.0.0.0";
  String port = "8888";
  String name = "Messi";

  IOWebSocketChannel? _socketClient;
  ConnectionStatus connectionStatus = ConnectionStatus.disconnected;

  String? mySocketId;
  List<String> clients = [];
  String selectedClient = "";
  int? selectedClientIndex;
  String messages = "";

  bool finished = false;
  bool file_saving = false;
  bool file_loading = false;

  AppData() {
    _getLocalIpAddress();
  }

  void _getLocalIpAddress() async {
    try {
      final List<NetworkInterface> interfaces = await NetworkInterface.list(
          type: InternetAddressType.IPv4, includeLoopback: false);
      if (interfaces.isNotEmpty) {
        final NetworkInterface interface = interfaces.first;
        final InternetAddress address = interface.addresses.first;
        ip = address.address;
        notifyListeners();
      }
    } catch (e) {
      // ignore: avoid_print
      print("Can't get local IP address : $e");
    }
  }

  void connectToServer() async {
    connectionStatus = ConnectionStatus.connecting;
    playersName.clear;
    notifyListeners();

    // Simulate connection delay
    await Future.delayed(const Duration(seconds: 1));

    _socketClient = IOWebSocketChannel.connect("ws://$ip:$port");
    _socketClient?.sink.add('{"type": "name", "value": "${name}"}');
    _socketClient!.stream.listen(
      (message) async {
        final data = jsonDecode(message);

        switch (data['type']) {
          case 'id':
            playersId.add(data['me']);
            playersId.add(data['enemy']);
            meActivePlayer = data['can'];
            playersName.add(data['enemyName']);
            cardFotos = setGameImages(data['cards']);
            torn = data['torn'];
            waiting = data['waiting'];
            break;
          case 'name':
            playersName.add(data['name']);
            break;
          case 'torn':
            changeRound();
            meActivePlayer = data['value'];
            break;
          case 'move':
            boldCard2Player(data['value']);
            break;
          case 'disconnected':
            if (playersId.contains(data['id']) && finished == false) {
              winnerPoints = 0;
              finished = true;
              winner = playersName[0];
              ranking.add(
                  "${playersName[0]} ha guanyat a ${playersName[1]} perque ${playersName[1]} s'ha desconectat");
              //Captura del tablero
              tableroResult = await screenshotController.capture();
              _socketClient!.sink.close();
              connectionStatus = ConnectionStatus.result;
              notifyListeners();
            }
          default:
            messages += "Message from '${data['from']}': ${data['value']}\n";
            break;
        }
        if (connectionStatus != ConnectionStatus.connected &&
            playersId != 2 &&
            !finished) {
          connectionStatus = ConnectionStatus.connected;
        }

        notifyListeners();
      },
      onError: (error) {
        connectionStatus = ConnectionStatus.disconnected;
        mySocketId = "";
        selectedClient = "";
        clients = [];
        messages = "";
        notifyListeners();
      },
      onDone: () {
        connectionStatus = ConnectionStatus.disconnected;
        if (finished) {
          connectionStatus = ConnectionStatus.result;
        }
        mySocketId = "";
        selectedClient = "";
        clients = [];
        messages = "";
        notifyListeners();
      },
    );
  }

  disconnectFromServer() async {
    connectionStatus = ConnectionStatus.disconnecting;
    notifyListeners();

    if (finished == false) {
      ranking.add(
          "${playersName[1]} ha guanyat a ${playersName[0]} perque ${playersName[0]} s'ha desconectat");
    }

    // Simulate connection delay
    await Future.delayed(const Duration(seconds: 1));

    _socketClient!.sink.close();
  }

  /*
  * Save file example:

    final myData = {
      'type': 'list',
      'clients': clients,
      'selectedClient': selectedClient,
      // i més camps que vulguis guardar
    };
    
    await saveFile('myData.json', myData);

  */

  Future<void> saveFile(String fileName, List<String> data) async {
    file_saving = true;
    notifyListeners();
    String res = "";

    try {
      final directory = await getApplicationDocumentsDirectory();
      final file = File('${directory.path}/$fileName');

      for (String f in data) {
        res += f + ";";
      }

      await file.writeAsString(res);
    } catch (e) {
      // ignore: avoid_print
      print("Error saving file: $e");
    } finally {
      file_saving = false;
      notifyListeners();
    }
  }

  /*
  * Read file example:
  
    final data = await readFile('myData.json');

  */

  Future<List<String>?> readFile(String fileName) async {
    file_loading = true;
    notifyListeners();
    List<String> data = [];

    try {
      final directory = await getApplicationDocumentsDirectory();
      final file = File('${directory.path}/$fileName');
      if (await file.exists()) {
        String infoFile = await file.readAsString();
        String record = "";
        for (int i = 0; i < infoFile.length; i++) {
          if (infoFile[i] == ';') {
            data.add(record);
            record = "";
          } else {
            record += infoFile[i];
          }
        }

        return data;
      } else {
        // ignore: avoid_print
        print("File does not exist!");
        return null;
      }
    } catch (e) {
      // ignore: avoid_print
      print("Error reading file: $e");
      return null;
    } finally {
      file_loading = false;
      notifyListeners();
    }
  }

  //Memory
  String winner = "";
  int winnerPoints = 0;
  List<String> playersName = ["prueba1", "prueba2"];
  List<String> playersId = [];
  List<int> playersScore = [0, 0];
  int torn = 0;
  int waiting = 1;
  bool meActivePlayer = true;
  List<String> defaultNames = [
    "Albert",
    "Akane",
    "Messi",
    "Mark",
    "Shawn",
    "Axel",
    "Mari",
    "Jose",
    "Jan",
    "Aura",
    "Marta"
  ];
  ScreenshotController screenshotController = ScreenshotController();
  Uint8List? tableroResult;
  Color textResult = Colors.black;
  List<String> ranking = []; // WinnerName    Oponent    1-0

  //Lista de imagenes
  List<String>? gameImages; //Aqui se pondran las fotos actuales de cada casilla

  //Las diferentes fotos que hay (luego estaran las cartas del juego, 2 de cada)
  List<String> cardFotos = [];

  //Para ver si las dos primeras clicadas son iguales o no
  List<Map<int, String>> pairCheck = [];
  final String interrogantePath = 'assets/images/hidden.png';
  final int cardCount = 16;

  void setName(String name, String name2) {
    playersName.add(name);
    playersName.add(name2);
    notifyListeners();
  }

  void changeRound() {
    int c = torn;
    torn = 0 + waiting;
    waiting = 0 + c;
  }

  void changeRoundMessage() {
    final message = {'type': 'torn', 'value': true, 'enemyId': playersId[1]};
    _socketClient!.sink.add(jsonEncode(message));
  }

  List<String> setGameImages(List<dynamic> indexCards) {
    List<String> result = [];

    for (int i = 0; i < indexCards.length; i++) {
      int carta = indexCards[i] as int;
      result.add(cardFotos[carta]);
    }
    return result;
  }

  void boldCard(int index) {
    if (meActivePlayer && gameImages![index] == interrogantePath) {
      //voltearla
      gameImages![index] = cardFotos[index];
      pairCheck.add({index: cardFotos[index]});
      moveMessage(index);

      //Si coinciden las dos volteadas
      if (pairCheck.length >= 2) {
        meActivePlayer = false;
        if (pairCheck[0].values.first == pairCheck[1].values.first) {
          playersScore[torn] += 1;
          checkWinner();
          pairCheck.clear();
          meActivePlayer = true;
        } else {
          Future.delayed(Duration(milliseconds: 600), () {
            gameImages![pairCheck[0].keys.first] = interrogantePath;
            gameImages![pairCheck[1].keys.first] = interrogantePath;
            pairCheck.clear();
            changeRound();
            meActivePlayer = false;
            //Canvi de torn
            changeRoundMessage();
            notifyListeners();
          });
        }
      }
    }
    notifyListeners();
  }

  void moveMessage(int index) {
    final msn = {'type': 'move', 'value': index, 'enemyId': playersId[1]};
    _socketClient!.sink.add(jsonEncode(msn));
  }

  void boldCard2Player(int index) {
    if (gameImages![index] == interrogantePath) {
      //voltearla
      gameImages![index] = cardFotos[index];
      pairCheck.add({index: cardFotos[index]});

      //Si coinciden las dos volteadas
      if (pairCheck.length >= 2) {
        meActivePlayer = false;
        if (pairCheck[0].values.first == pairCheck[1].values.first) {
          playersScore[torn] += 1;
          checkWinner();
          pairCheck.clear();
        } else {
          Future.delayed(Duration(milliseconds: 600), () {
            gameImages![pairCheck[0].keys.first] = interrogantePath;
            gameImages![pairCheck[1].keys.first] = interrogantePath;
            pairCheck.clear();
            notifyListeners();
          });
        }
      }
    }
    notifyListeners();
  }

  //Resetear la AppData para poder volver a jugar
  void restart() {
    winnerPoints = 0;
    finished = false;
    winner = "";
    playersName = ["prueba1", "prueba2"];
    pairCheck = [];
    playersScore = [0, 0];
    playersId = [];
    cardFotos = [
      'assets/images/card0.png',
      'assets/images/card1.png',
      'assets/images/card2.png',
      'assets/images/card3.png',
      'assets/images/card4.png',
      'assets/images/card5.jpg',
      'assets/images/card6.png',
      'assets/images/card7.png',
    ];
    gameImages = [
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
      'assets/images/hidden.png',
    ];
    textResult = Colors.black;
  }

  void goToLobby() {
    connectionStatus = ConnectionStatus.disconnected;
    notifyListeners();
  }

  Future<void> checkWinner() async {
    int num = 0;
    for (int cn = 0; cn < gameImages!.length; cn++) {
      if (gameImages![cn] != interrogantePath) {
        num += 1;
      }
    }
    if (num == cardCount) {
      //Captura del tablero
      tableroResult = await screenshotController.capture();

      if (playersScore[0] > playersScore[1]) {
        winner = playersName[0];
        winnerPoints = playersScore[0];
        ranking.add(
            "${playersName[0]} ha guanyat a ${playersName[1]} per ${playersScore[0]}-${playersScore[1]}");
      } else if (playersScore[0] < playersScore[1]) {
        winner = playersName[1];
        winnerPoints = playersScore[1];
        ranking.add(
            "${playersName[1]} ha guanyat a ${playersName[0]} per ${playersScore[1]}-${playersScore[0]}");
      } else if (playersScore[0] == playersScore[1]) {
        winnerPoints = -1;
        ranking.add(
            "${playersName[1]} y ${playersName[0]} han empatat amb ${playersScore[1]}-${playersScore[0]}");
      }
      finished = true;
      _socketClient!.sink.close();
      connectionStatus = ConnectionStatus.result;
    }
    notifyListeners();
  }
}

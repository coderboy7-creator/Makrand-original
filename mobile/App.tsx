import React, { useEffect, useState } from "react";
import { NavigationContainer, DefaultTheme } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import {
  ActivityIndicator, Button, ScrollView, StyleSheet, Text, TextInput, View
} from "react-native";
import { StatusBar } from "expo-status-bar";

const API = (global as any).MAKARANDA_API || "http://127.0.0.1:8080";
const Stack = createNativeStackNavigator();
const theme = {
  ...DefaultTheme,
  colors: { ...DefaultTheme.colors, background: "#0B1026", card: "#141A33", text: "#F7F1E3", primary: "#C9A227" },
};

function Home({ navigation }: any) {
  const [p, setP] = useState<any>(null);
  useEffect(() => {
    fetch(API + "/api/v1/jyotish/panchang").then((r) => r.json()).then(setP).catch(() => setP({ error: "Backend offline" }));
  }, []);
  return (
    <ScrollView contentContainerStyle={styles.wrap}>
      <Text style={styles.h}>मकरन्द ज्योतिष</Text>
      <Text style={styles.sub}>Mithilanchal · Makaranda Panchang</Text>
      {p ? (
        <View style={styles.card}>
          <Text style={styles.gold}>{p.date || ""}</Text>
          <Text style={styles.body}>{p.vara} · {p.paksha} {p.tithi}</Text>
          <Text style={styles.body}>{p.nakshatra} · {p.yoga}</Text>
          <Text style={styles.caption}>{p.makarandaNote}</Text>
        </View>
      ) : <ActivityIndicator color="#C9A227" />}
      <Button title="Cast Kundali" color="#C9A227" onPress={() => navigation.navigate("Kundali")} />
      <View style={{ height: 8 }} />
      <Button title="Daily Horoscope" color="#E07A2F" onPress={() => navigation.navigate("Horoscope")} />
    </ScrollView>
  );
}

function Kundali() {
  const [name, setName] = useState("Native");
  const [out, setOut] = useState<any>(null);
  const [busy, setBusy] = useState(false);
  const cast = async () => {
    setBusy(true);
    const dateTime = new Date().toISOString().slice(0, 19);
    const r = await fetch(API + "/api/v1/jyotish/kundali", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        name, dateTime, timeZone: "Asia/Kolkata", tzOffsetHours: 5.5,
        latitude: 26.1542, longitude: 85.8918, place: "Darbhanga, Bihar, India",
        ayanamsa: "SURYA_SIDDHANTA_MAKARANDA", panchangMode: "SIDDHANTIC", houseSystem: "WHOLE_SIGN"
      }),
    });
    setOut(await r.json());
    setBusy(false);
  };
  return (
    <ScrollView contentContainerStyle={styles.wrap}>
      <TextInput style={styles.input} value={name} onChangeText={setName} placeholder="Name" placeholderTextColor="#888" />
      <Button title={busy ? "Calculating..." : "Cast (Darbhanga now)"} onPress={cast} color="#C9A227" />
      {out?.lagna && (
        <View style={styles.card}>
          <Text style={styles.gold}>Lagna {out.lagna.signSa}</Text>
          <Text style={styles.body}>{out.lagna.signDegree}</Text>
          <Text style={styles.caption}>{out.ayanamsaLabel} · {out.panchangMode}</Text>
          {Object.values(out.planets || {}).map((p: any) => (
            <Text key={p.name} style={styles.body}>{p.name} · {p.signSa} · H{p.house} · {p.nakshatra}</Text>
          ))}
        </View>
      )}
    </ScrollView>
  );
}

function Horoscope() {
  const [data, setData] = useState<any>(null);
  useEffect(() => { fetch(API + "/api/v1/jyotish/horoscope").then((r) => r.json()).then(setData); }, []);
  return (
    <ScrollView contentContainerStyle={styles.wrap}>
      {(data?.rashis || []).map((r: any) => (
        <View key={r.sign} style={styles.card}>
          <Text style={styles.gold}>{r.sanskrit} ({r.sign})</Text>
          <Text style={styles.caption}>Lucky {r.luckyColour} · {r.luckyNumber}</Text>
          <Text style={styles.body}>{r.prediction}</Text>
        </View>
      ))}
    </ScrollView>
  );
}

export default function App() {
  return (
    <NavigationContainer theme={theme}>
      <StatusBar style="light" />
      <Stack.Navigator screenOptions={{ headerStyle: { backgroundColor: "#12081A" }, headerTintColor: "#C9A227" }}>
        <Stack.Screen name="Home" component={Home} options={{ title: "Makaranda" }} />
        <Stack.Screen name="Kundali" component={Kundali} />
        <Stack.Screen name="Horoscope" component={Horoscope} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  wrap: { padding: 16, backgroundColor: "#0B1026", minHeight: "100%" },
  h: { color: "#C9A227", fontSize: 28, fontWeight: "700" },
  sub: { color: "#C4BBA8", marginBottom: 16 },
  gold: { color: "#C9A227", fontSize: 18, fontWeight: "700" },
  body: { color: "#F7F1E3", marginTop: 4 },
  caption: { color: "#C4BBA8", marginTop: 6, fontSize: 12 },
  card: { backgroundColor: "#141A33", padding: 14, borderRadius: 12, marginVertical: 10, borderColor: "rgba(201,162,39,0.3)", borderWidth: 1 },
  input: { borderColor: "#C9A227", borderWidth: 1, color: "#F7F1E3", padding: 10, borderRadius: 8, marginBottom: 12 },
});

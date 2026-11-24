# Tuneify Design System — Swiss-Bauhaus

Tuneify follows a **Swiss-Bauhaus** design system: **Bauhaus structure, Swiss finishing.**

Two historical references, deliberately combined:

- **Bauhaus (1919–1933)** gives the app its *structure* — geometric shapes, a restricted primary
  triad, editorial type hierarchy, asymmetric layout. This is the part people remember.
- **Swiss / International Typographic Style (1950s)** gives the app its *finishing* — generous
  whitespace, hairline rules, flat color-blocking instead of drop shadows, and typography doing the
  hierarchy work. This is the part that makes it feel calm and premium instead of noisy.

**Primary principle:** bold and editorial where users *browse* music; quiet and immersive where
users *listen* to music.

This document is the definitive guide for designing, implementing, and extending UI across the
Tuneify Android application.

---

## 0. What this system is NOT

These were tried in this codebase and are now explicitly retired. Do not reintroduce them.

| Retired | Why |
| :--- | :--- |
| **Backdrop blur / glassmorphism** (`liquidGlass`) | Re-records the whole subtree every frame via `RenderEffect`. Biggest source of dropped frames on mid-range GPUs, and it muddies the fixed triad into grey. |
| **Material You wallpaper dynamic color** | Fights the fixed triad. A 3-color Bauhaus palette and wallpaper-derived colors cannot coexist. |
| **Neo-brutalist shadow spam** | Hard shadows on every card reads stiff and hobbyist, and is unreadable in dark mode. |
| **Near-white borders in dark mode** | A cream `1.dp` outline around every card is physically painful at night. |
| **Pill-everything / 24dp+ radii** | Destroys the geometric identity. |

---

## 1. Core Visual Identity

- **Strong Typography:** Outfit (geometric sans) for display and headlines with tight negative
  tracking. Neutral system face for body copy.
- **Crisp Rectangular Surfaces:** Small controlled radii (4 / 8 / 16 dp). No pills except literal
  toggles and segmented controls.
- **Flat by default:** hierarchy comes from color-blocking and hairlines, not shadows.
- **Restrained Triad:** Yellow `#F8CC2E`, Red `#E4342B`, Blue `#2F5BD8`.
- **Artwork as Hero:** album art is framed cleanly at 4dp with a hairline in dark mode only.
- **One motif:** the three-bar mark (`TuneifyTriBar`) repeats everywhere as the brand signature.

---

## 2. Hard rules

These four rules cause more visual damage than any individual component choice. Follow them.

1. **One accent per screen.** Yellow is reserved for the single primary action on a screen — never
   decoration. Red means like / live / error. Blue means info / shuffle / links.
2. **Shadows on exactly three surfaces only:** now-playing artwork, the main play button, and the
   active navigation indicator. Everything else separates with color + hairline.
3. **Content cards get no border.** Only *interactive* elements get a `1.5dp` outline.
4. **No blur, anywhere.** `liquidGlass` is retained only as a compile shim that paints a flat
   surface while call sites are migrated.

---

## 3. Color Tokens

Tokens are centralized in
[`TuneifyDesignTokens.kt`](file:///app/src/main/kotlin/com/music/echo/ui/theme/TuneifyDesignTokens.kt)
and exposed via `TuneifyTokens` and `LocalTuneifyColors`.

### Light theme — warm paper
| Token | Hex | Role |
| :--- | :--- | :--- |
| `Light.Background` | `#F5F3EE` | App background |
| `Light.Surface` | `#FFFFFF` | Cards, sheets, dialogs |
| `Light.Elevated` | `#EBE8E0` | Containers, search bars, secondary panels |
| `Light.TextPrimary` | `#0F0F0F` | Titles, prominent headers |
| `Light.TextSecondary` | `#6B6B6B` | Metadata, artist subtitles, timestamps |
| `Light.Border` | `#0F0F0F` | **Interactive elements only** |
| `Light.BorderSubtle` | `#D8D4CB` | Hairlines, dividers, card edges |

### Dark theme — neutral slate
| Token | Hex | Role |
| :--- | :--- | :--- |
| `Dark.Background` | `#121212` | App background |
| `Dark.Surface` | `#1B1B1B` | Cards, sheets, dialogs |
| `Dark.Elevated` | `#242424` | Containers, search bars, secondary panels |
| `Dark.TextPrimary` | `#F2F0EB` | Titles, prominent headers |
| `Dark.TextSecondary` | `#A8A5A0` | Metadata, artist subtitles, timestamps |
| `Dark.Border` | `#F2F0EB` @ 24% | **Interactive elements only** |
| `Dark.BorderSubtle` | `#F2F0EB` @ 10% | Hairlines, dividers, card edges |
| `Dark.BorderHigh` | `#F2F0EB` @ 40% | Hero artwork frames, focused controls |

### Accent triad
| Token | Hex | Role |
| :--- | :--- | :--- |
| `PrimaryYellow` | `#F8CC2E` | Primary action, active tab, main play button |
| `SecondaryRed` | `#E4342B` | Liked state, live, errors, active repeat |
| `SupportingBlue` | `#2F5BD8` | Info badges, active shuffle, links |
| `OnYellow` | `#0F0F0F` | Always high-contrast foreground on yellow |

Users may override the accent from settings; that override re-tones **`primary` only**. Surfaces,
borders and text never change with the accent choice.

**Material roles.** `ColorScheme.secondary` is a *neutral text role* in Tuneify (it maps to
`Light.TextSecondary` / `Dark.TextSecondary`), not red. The accent red is exposed only through
the explicit `TuneifyTokens.Red` token, because ~55 call sites across the app use `secondary`
for metadata copy. `ColorScheme.tertiary` is likewise neutral. Never assume `secondary` means
"red" in this codebase.

**Player background.** `PlayerBackgroundStyle.DEFAULT` is the default; the previous `GRADIENT`
default bled album-art colours into the text area and broke the fixed palette. Other styles
(BLUR, GLOW_ANIMATED, …) remain opt-in.

---

## 4. Geometry, Spacing & Elevation

| Level | Radius | Usage |
| :--- | :--- | :--- |
| **Sharp** | `0.dp` | Full-bleed dividers, docking bar bottom edge |
| **Small** | `4.dp` | Thumbnails, badges, chips, small indicators |
| **Medium** | `8.dp` | Buttons, cards, list containers, dialogs |
| **Sheet** | `16.dp` | Bottom sheets, floating player chrome |

Spacing lives on an 8dp grid: `TuneifyTokens.Space` (`Xs 4`, `Sm 8`, `Md 12`, `Lg 16`, `Xl 24`,
`Xxl 32`), with `Gutter = 16.dp` for screen edges and `SectionGap = 28.dp` between top-level
sections.

### Offset shadows
```kotlin
// Hero surfaces only.
Modifier.tuneifyHeroShadow(isDark = isDark, shape = TuneifyTokens.ShapeCard)
```
`neoShadow` draws a hard, unblurred offset that correctly follows the shape's corner radius.

### Cards
```kotlin
Modifier.neoCard(isDark = isDark) // flat surface + hairline, no shadow
Modifier.neoCard(isDark = isDark, prominent = true) // 1.5dp outline, for interactive cards
```

---

## 5. Typography

| Role | Face | Size / tracking | Usage |
| :--- | :--- | :--- | :--- |
| Display | Outfit Bold | 52 / -1.5sp | Hero titles |
| Headline | Outfit Bold | 30 / -0.8sp | Screen titles |
| Title | Outfit SemiBold | 20 / -0.3sp | Section headers, card titles |
| Body | System / chosen font | 14 / +0.1sp | Descriptions, metadata |
| Label | System | 11 / +1.0sp | Uppercase eyebrows only |

**Uppercase + wide tracking belongs to 11sp eyebrows only.** Never uppercase a title.

---

## 6. Motion

Motion — not shadow — is what makes the app feel physical. Tokens live in `TuneifyMotion`.

| Moment | Spec |
| :--- | :--- |
| Artwork → Player | Shared-element expand, `TuneifyMotion.Slow` (320ms), `signature()` spring |
| Play button press | Scale `0.94` + shadow collapse, `TuneifyMotion.Fast` (140ms), `snappy()` return |
| Lyrics line change | `Base` (220ms) fade + 2dp rise; inactive lines dimmed to 55% |
| List entry | First 8 items, 40ms stagger, fade + 8dp rise, once per screen |
| Tab / nav indicator | `Base` (220ms) spring slide |

Use `Modifier.tuneifyPressScale(interactionSource)` for press feedback.

---

## 7. Signature motif

`TuneifyTriBar` — three ascending bars in red / yellow / blue — is the app's mark. Repeat it, do not
invent new decorations:

- `TuneifyEyebrow` above hero content
- `TuneifySectionHeader` for every section title
- `TuneifyLogo` in the top bar
- Empty states and dialog headers

One motif in many places builds recall; ten motifs build noise.

---

## 8. Reusable components

| Component | Where | Notes |
| :--- | :--- | :--- |
| `TuneifyTriBar` | `ui/component/TuneifySignature.kt` | The motif |
| `TuneifyEyebrow` | same | Uppercase label + tick |
| `TuneifySectionHeader` | same | Tri-bar + title + trailing action |
| `Modifier.tuneifyPressScale` | same | Press feedback |
| `Modifier.tuneifyHeroShadow` | same | One of the three allowed shadows |
| `neoCard` / `neoShadow` | `ui/theme/TuneifyDesignTokens.kt` | Flat cards, hero shadow |
| `TuneifyLogo` | `ui/component/TuneifyClay.kt` | Equalizer mark + wordmark |

### Full-screen player
Immersive and quiet. Central square artwork at 4dp with a hairline frame and the hero shadow. One
dominant yellow play button. Secondary controls are flat rectangles with no shadow. No blur, no
gradient blobs, no album-art tint bleeding into text areas.

### Mini-player & navigation
Floating rectangular chrome at `ShapeSheet` (16dp), flat surface color, hairline edge. The active
destination gets a flat yellow block. No glass, no elevated shadows.

### Dialogs & bottom sheets
`ShapeMedium` (8dp) for dialogs, `ShapeSheet` (16dp) top corners for sheets. Flat surface,
hairline edge, optional hero shadow on the dialog only.
